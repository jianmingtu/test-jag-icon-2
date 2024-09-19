package ca.bc.gov.open.sender.services;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.hsr.*;
import ca.bc.gov.open.icon.hsrservice.SubmitHealthServiceRequest;
import ca.bc.gov.open.icon.hsrservice.SubmitHealthServiceRequestResponse;
import ca.bc.gov.open.icon.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
@Slf4j
public class HSRService {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    @Value("${icon.hsr-service-url}")
    private String hsrServiceUrl = "https://127.0.0.1/";

    private static int retries;
    private static int MAX_RETRIES = 5;
    private static int PAUSE = 5000; // in milliseconds
    private boolean appErr = false;
    private boolean hsrFail = false;

    private final WebServiceTemplate webServiceTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public HSRService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            WebServiceTemplate webServiceTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.webServiceTemplate = webServiceTemplate;
    }

    public void processHSR(HealthServicePub publishHSR)
            throws InterruptedException, JsonProcessingException {
        retries = 0;

        // Submit HSR (Invoke SOAP Service)
        SubmitHealthServiceRequest submitHealthServiceRequest = new SubmitHealthServiceRequest();
        submitHealthServiceRequest.setCsNumber(publishHSR.getCsNum());
        submitHealthServiceRequest.setSubmissionDate(publishHSR.getRequestDate());
        submitHealthServiceRequest.setCentre(publishHSR.getLocation());
        submitHealthServiceRequest.setDetails(publishHSR.getHealthRequest());
        while (retries < MAX_RETRIES) {
            try {
                SubmitHealthServiceRequestResponse submitHealthServiceRequestResponse =
                        (SubmitHealthServiceRequestResponse)
                                webServiceTemplate.marshalSendAndReceive(
                                        hsrServiceUrl, submitHealthServiceRequest);
                publishHSR.setPacId(
                        String.valueOf(
                                submitHealthServiceRequestResponse
                                        .getSubmitHealthServiceRequestReturn()));
                log.info(
                        objectMapper.writeValueAsString(
                                new RequestSuccessLog("Request Success", "submitHSR")));
                break;
            } catch (WebServiceIOException ex) {
                // Connection Error
                appErr = false;
                hsrFail = true;
                break;
            } catch (Exception ex) {
                if (++retries == MAX_RETRIES) {
                    appErr = true;
                    hsrFail = true;
                    break;
                }
                Thread.sleep(PAUSE);
            }
        }

        // Record HSR
        UriComponentsBuilder recordBuilder =
                UriComponentsBuilder.fromHttpUrl(host + "health/record-hsr");
        var recordReq = new RecordHSRRequest();
        recordReq.setCsNum(publishHSR.getCsNum());
        recordReq.setHsrId(publishHSR.getHsrId());
        recordReq.setPacId(publishHSR.getPacId());
        try {
            HttpEntity<Map<String, String>> resp =
                    restTemplate.exchange(
                            recordBuilder.toUriString(),
                            HttpMethod.POST,
                            new HttpEntity<>(recordReq, new HttpHeaders()),
                            new ParameterizedTypeReference<>() {});
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "recordHSR")));
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "recordHSR",
                                    ex.getMessage(),
                                    recordReq)));
            throw handleError(ex);
        }

        // Notification HSR only if hsr fails
        if (hsrFail) {
            String errMsg =
                    appErr
                            ? "HSR Transmission - Application Error - CS Number("
                                    + publishHSR.getCsNum()
                                    + ")"
                            : "HSR Transmission - Connection Error - CS Number("
                                    + publishHSR.getCsNum()
                                    + ")";
            UriComponentsBuilder notificationBuilder =
                    UriComponentsBuilder.fromHttpUrl(host + "health/notification-hsr");
            var notificationReq = new NotificationHSRRequest();
            notificationReq.setNotificationMessageText(errMsg);
            try {
                HttpEntity<Map<String, String>> resp =
                        restTemplate.exchange(
                                notificationBuilder.toUriString(),
                                HttpMethod.POST,
                                new HttpEntity<>(notificationReq, new HttpHeaders()),
                                new ParameterizedTypeReference<>() {});
                log.info(
                        objectMapper.writeValueAsString(
                                new RequestSuccessLog("Request Success", "notificationHSR")));
            } catch (Exception ex) {
                log.error(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from ORDS",
                                        "notificationHSR",
                                        ex.getMessage(),
                                        notificationReq)));
                throw handleError(ex);
            }
        }
        // End of BPM
    }
}
