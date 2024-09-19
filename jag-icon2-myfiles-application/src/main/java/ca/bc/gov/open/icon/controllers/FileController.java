package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.configuration.SoapConfig;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.myfiles.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@Slf4j
public class FileController {

    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public FileController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getClientClaims")
    @ResponsePayload
    public GetClientClaimsResponse getClientClaims(@RequestPayload GetClientClaims getClientClaims)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "files/claims")
                        .queryParam("directedIdentifier", getClientClaims.getDirectedIdentifier());

        try {
            HttpEntity<Claims> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            Claims.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getClientClaims")));
            GetClientClaimsResponse out = new GetClientClaimsResponse();
            out.setClaims(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getClientClaims",
                                    ex.getMessage(),
                                    getClientClaims)));
            throw handleError(ex, new ca.bc.gov.open.icon.myfiles.Error());
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getCsNumsByDate")
    @ResponsePayload
    public GetCsNumsByDateResponse getCsNumsByDate(@RequestPayload GetCsNumsByDate getCsNumsByDate)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "files/csnumsbydate")
                        .queryParam("startDate", getCsNumsByDate.getStartDate())
                        .queryParam("endDate", getCsNumsByDate.getEndDate());

        try {
            HttpEntity<GetCsNumsByDateResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            GetCsNumsByDateResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getCsNumsByDate")));

            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getCsNumsByDate",
                                    ex.getMessage(),
                                    getCsNumsByDate)));
            throw handleError(ex, new ca.bc.gov.open.icon.myfiles.Error());
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getAgencyFile")
    @ResponsePayload
    public GetAgencyFileResponse getAgencyFile(@RequestPayload GetAgencyFile getAgencyFile)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "files/agency-file")
                        .queryParam("agencyFileNo", getAgencyFile.getAgencyFileNo())
                        .queryParam("agencyIdCd", getAgencyFile.getAgencyIdCd());

        try {
            HttpEntity<AgencyFile> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            AgencyFile.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getAgencyFile")));
            GetAgencyFileResponse out = new GetAgencyFileResponse();
            out.setAgencyFile(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getAgencyFile",
                                    ex.getMessage(),
                                    getAgencyFile)));
            throw handleError(ex, new ca.bc.gov.open.icon.myfiles.Error());
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getClientInfo")
    @ResponsePayload
    public GetClientInfoResponse getClientInfo(@RequestPayload GetClientInfo getClientInfo)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "files/client-info")
                        .queryParam("csNum", getClientInfo.getCsNum());

        try {
            HttpEntity<GetClientInfoResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            GetClientInfoResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getClientInfo")));
            resp.getBody();
            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getClientInfo",
                                    ex.getMessage(),
                                    getClientInfo)));
            throw handleError(ex, new ca.bc.gov.open.icon.myfiles.Error());
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "setMessage")
    @ResponsePayload
    public SetMessageResponse setMessage(@RequestPayload SetMessage setMessage)
            throws JsonProcessingException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "files/message");
        HttpEntity<SetMessage> payload = new HttpEntity<>(setMessage, new HttpHeaders());

        try {
            HttpEntity<SetMessageResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            SetMessageResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "setMessage")));

            return resp.getBody();

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "setMessage",
                                    ex.getMessage(),
                                    setMessage)));
            throw handleError(ex, new ca.bc.gov.open.icon.myfiles.Error());
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "setDisclosure")
    @ResponsePayload
    public SetDisclosureResponse setDisclosure(@RequestPayload SetDisclosure setDisclosure)
            throws JsonProcessingException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "files/disclosure");
        HttpEntity<SetDisclosure> payload = new HttpEntity<>(setDisclosure, new HttpHeaders());

        try {
            HttpEntity<SetDisclosureResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            SetDisclosureResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "setDisclosure")));

            return resp.getBody();

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "setDisclosure",
                                    ex.getMessage(),
                                    setDisclosure)));
            throw handleError(ex, new ca.bc.gov.open.icon.myfiles.Error());
        }
    }
}
