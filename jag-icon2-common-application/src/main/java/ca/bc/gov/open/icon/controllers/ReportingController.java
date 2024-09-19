package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.audit.EReportAnswers;
import ca.bc.gov.open.icon.audit.EReportAnswersSubmitted;
import ca.bc.gov.open.icon.audit.EReportAnswersSubmittedResponse;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.ereporting.*;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
public class ReportingController {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReportingController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(namespace = "ICON2.Source.Audit.ws:Record", localPart = "eReportAnswersSubmitted")
    @ResponsePayload
    public EReportAnswersSubmittedResponse eReportAnswersSubmitted(
            @RequestPayload EReportAnswersSubmitted eReportAnswersSubmitted)
            throws JsonProcessingException {

        var inner =
                eReportAnswersSubmitted.getEReportAnswers() != null
                        ? eReportAnswersSubmitted.getEReportAnswers()
                        : new EReportAnswers();

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/ereport-answers-submitted");
        HttpEntity<EReportAnswers> payload = new HttpEntity<>(inner, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "eReportAnswersSubmitted")));
            EReportAnswersSubmittedResponse out = new EReportAnswersSubmittedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "eReportAnswersSubmitted",
                                    ex.getMessage(),
                                    inner)));
            throw handleError(ex, new ca.bc.gov.open.icon.audit.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "getReportingCmpltInstruction")
    @ResponsePayload
    public GetReportingCmpltInstructionResponse getReportingCmpltInstruction(
            @RequestPayload GetReportingCmpltInstruction getReportingCmpltInstruction)
            throws JsonProcessingException {

        GetReportingCmpltInstructionDocument getReportingCmpltInstructionDocument =
                new GetReportingCmpltInstructionDocument();
        getReportingCmpltInstructionDocument.setReportingCmpltInstruction(
                XMLUtilities.deserializeXmlStr(
                        getReportingCmpltInstruction.getXMLString(),
                        new ReportingCmpltInstruction()));
        getReportingCmpltInstructionDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getReportingCmpltInstruction.getUserTokenString(),
                        new ca.bc.gov.open.icon.ereporting.UserToken()));

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/complete-instruction");
        HttpEntity<GetReportingCmpltInstructionDocument> payload =
                new HttpEntity<>(getReportingCmpltInstructionDocument, new HttpHeaders());

        try {
            HttpEntity<ReportingCmpltInstruction> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            ReportingCmpltInstruction.class);

            GetReportingCmpltInstructionResponse getReportingCmpltInstructionResponse =
                    new GetReportingCmpltInstructionResponse();
            getReportingCmpltInstructionDocument.setReportingCmpltInstruction(resp.getBody());
            getReportingCmpltInstructionResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(
                            getReportingCmpltInstructionDocument.getReportingCmpltInstruction()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog(
                                    "Request Success", "getReportingCmpltInstruction")));

            return getReportingCmpltInstructionResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getReportingCmpltInstruction",
                                    ex.getMessage(),
                                    getReportingCmpltInstruction)));

            throw handleError(ex, new ca.bc.gov.open.icon.ereporting.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "getLocations")
    @ResponsePayload
    public GetLocationsResponse getLocationsResponse(@RequestPayload GetLocations getLocations)
            throws JsonProcessingException {

        GetLocationsDocument getLocationsDocument = new GetLocationsDocument();
        getLocationsDocument.setLocations(
                XMLUtilities.deserializeXmlStr(getLocations.getXMLString(), new Locations()));
        getLocationsDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getLocations.getUserTokenString(),
                        new ca.bc.gov.open.icon.ereporting.UserToken()));

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/locations");
        HttpEntity<GetLocationsDocument> payload =
                new HttpEntity<>(getLocationsDocument, new HttpHeaders());

        try {
            HttpEntity<Locations> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Locations.class);

            GetLocationsResponse getLocationsResponse = new GetLocationsResponse();
            getLocationsDocument.setLocations(resp.getBody());
            getLocationsResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(getLocationsDocument.getLocations()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getLocationsResponse")));

            return getLocationsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getLocationsResponse",
                                    ex.getMessage(),
                                    getLocations)));

            throw handleError(ex, new ca.bc.gov.open.icon.ereporting.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "submitAnswers")
    @ResponsePayload
    public SubmitAnswersResponse submitAnswers(@RequestPayload SubmitAnswers submitAnswers)
            throws JsonProcessingException {

        SubmitAnswersDocument submitAnswersDocument = new SubmitAnswersDocument();
        submitAnswersDocument.setEReport(
                XMLUtilities.deserializeXmlStr(submitAnswers.getXMLString(), new Ereport()));
        submitAnswersDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        submitAnswers.getUserTokenString(),
                        new ca.bc.gov.open.icon.ereporting.UserToken()));

        int i = 0;
        for (var questions : submitAnswersDocument.getEReport().getQuestion()) {
            questions.setQuestionId(Integer.toString(i++));
        }

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/submit-answers");
        HttpEntity<SubmitAnswersDocument> payload =
                new HttpEntity<>(submitAnswersDocument, new HttpHeaders());

        try {

            restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    payload,
                    new ParameterizedTypeReference<>() {});

            SubmitAnswersResponse submitAnswersResponse = new SubmitAnswersResponse();
            submitAnswersResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(submitAnswersDocument.getEReport()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "submitAnswers")));

            return submitAnswersResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "submitAnswers",
                                    ex.getMessage(),
                                    submitAnswers)));

            throw handleError(ex, new ca.bc.gov.open.icon.ereporting.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "getAppointment")
    @ResponsePayload
    public GetAppointmentResponse getAppointment(@RequestPayload GetAppointment getAppointment)
            throws JsonProcessingException, JAXBException, UnsupportedEncodingException {

        GetAppointmentDocument getAppointmentDocument = new GetAppointmentDocument();
        getAppointmentDocument.setAppointment(
                XMLUtilities.deserializeXmlStr(getAppointment.getXMLString(), new Appointment()));
        getAppointmentDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getAppointment.getUserTokenString(),
                        new ca.bc.gov.open.icon.ereporting.UserToken()));

        HttpEntity<GetAppointmentDocument> payload =
                new HttpEntity<>(getAppointmentDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/appointment");

        try {
            HttpEntity<Appointment> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Appointment.class);

            GetAppointmentResponse getAppointmentResponse = new GetAppointmentResponse();
            getAppointmentDocument.setAppointment(resp.getBody());
            getAppointmentResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(getAppointmentDocument.getAppointment()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getAppointment")));

            return getAppointmentResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getAppointment",
                                    ex.getMessage(),
                                    getAppointment)));

            throw handleError(ex, new ca.bc.gov.open.icon.ereporting.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "getQuestions")
    @ResponsePayload
    public GetQuestionsResponse getQuestions(@RequestPayload GetQuestions getQuestions)
            throws JsonProcessingException {

        GetQuestionsDocument getQuestionsDocument = new GetQuestionsDocument();
        getQuestionsDocument.setEReport(
                XMLUtilities.deserializeXmlStr(getQuestions.getXMLString(), new Ereport()));

        getQuestionsDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getQuestions.getUserTokenString(),
                        new ca.bc.gov.open.icon.ereporting.UserToken()));

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "reporting/questions");
        HttpEntity<GetQuestionsDocument> payload =
                new HttpEntity<>(getQuestionsDocument, new HttpHeaders());

        try {
            HttpEntity<Ereport> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Ereport.class);

            GetQuestionsResponse getQuestionsResponse = new GetQuestionsResponse();
            getQuestionsDocument.getEReport().getQuestion().clear();
            getQuestionsDocument.getEReport().getQuestion().addAll(resp.getBody().getQuestion());
            getQuestionsResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(getQuestionsDocument.getEReport()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getQuestions")));

            return getQuestionsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getQuestions",
                                    ex.getMessage(),
                                    getQuestions)));

            throw handleError(ex, new ca.bc.gov.open.icon.ereporting.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.EReporting.ws.provider:EReporting",
            localPart = "getStatus")
    @ResponsePayload
    public GetStatusResponse getStatus(@RequestPayload GetStatus getStatus)
            throws JsonProcessingException {

        GetStatusDocument getStatusDocument = new GetStatusDocument();
        getStatusDocument.setStatus(
                XMLUtilities.deserializeXmlStr(
                        getStatus.getXMLString(), new ca.bc.gov.open.icon.ereporting.Status()));
        getStatusDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getStatus.getUserTokenString(),
                        new ca.bc.gov.open.icon.ereporting.UserToken()));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "reporting/status");
        HttpEntity<GetStatusDocument> payload =
                new HttpEntity<>(getStatusDocument, new HttpHeaders());

        try {
            HttpEntity<ca.bc.gov.open.icon.ereporting.Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            ca.bc.gov.open.icon.ereporting.Status.class);

            GetStatusResponse getStatusResponse = new GetStatusResponse();
            getStatusDocument.setStatus(resp.getBody());
            getStatusResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(getStatusDocument.getStatus()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getStatus")));

            return getStatusResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getStatus",
                                    ex.getMessage(),
                                    getStatus)));

            throw handleError(ex, new ca.bc.gov.open.icon.ereporting.Error());
        }
    }
}
