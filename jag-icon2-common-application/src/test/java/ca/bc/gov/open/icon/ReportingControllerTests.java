package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.Base;
import ca.bc.gov.open.icon.audit.EReportAnswers;
import ca.bc.gov.open.icon.audit.EReportAnswersSubmitted;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.controllers.*;
import ca.bc.gov.open.icon.ereporting.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReportingControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private WebServiceTemplate webServiceTemplate;
    @Mock private RestTemplate restTemplate;
    @Mock private ReportingController reportingController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reportingController = Mockito.spy(new ReportingController(restTemplate, objectMapper));
    }

    @Test
    public void testEReportAnswersSubmitted() throws JsonProcessingException {
        var req = new EReportAnswersSubmitted();
        var eReportAnswers = new EReportAnswers();
        var base = new Base();
        base.setSessionID("A");
        base.setCsNumber("A");
        base.setSessionID("A");
        eReportAnswers.setBase(base);
        eReportAnswers.setServiceCD("A");
        eReportAnswers.setFunctionCD("A");
        eReportAnswers.setEReportingEventID("A");

        req.setEReportAnswers(eReportAnswers);

        var status = new Status();
        ResponseEntity<Status> responseEntity = new ResponseEntity<>(status, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Status>>any()))
                .thenReturn(responseEntity);

        var resp = reportingController.eReportAnswersSubmitted(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetReportingCmpltInstruction() throws JsonProcessingException {
        var req = new GetReportingCmpltInstruction();
        var reportingCmpltInstruction = new ReportingCmpltInstruction();
        reportingCmpltInstruction.setCsNum("A");
        reportingCmpltInstruction.setText("A");
        req.setXMLString("A");

        var userToken = new ca.bc.gov.open.icon.ereporting.UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");
        req.setUserTokenString("A");

        ResponseEntity<ReportingCmpltInstruction> responseEntity =
                new ResponseEntity<>(reportingCmpltInstruction, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<ReportingCmpltInstruction>>any()))
                .thenReturn(responseEntity);

        var resp = reportingController.getReportingCmpltInstruction(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetLocationsResponse()
            throws JsonProcessingException, JAXBException, UnsupportedEncodingException {
        var req = new GetLocations();
        var locations = new Locations();
        var Location = new Location();
        Location.setLocationCd("A");
        locations.getLocation().add(Location);
        req.setXMLString("A");

        req.setUserTokenString("A");
        ResponseEntity<Locations> responseEntity = new ResponseEntity<>(locations, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Locations>>any()))
                .thenReturn(responseEntity);

        var resp = reportingController.getLocationsResponse(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testSubmitAnswers() throws JsonProcessingException {
        var req = new SubmitAnswers();
        req.setXMLString(
                "<EReport>\n"
                        + "    <csNum>1</csNum>\n"
                        + "    <eventId>1</eventId>\n"
                        + "    <pacID>1</pacID>\n"
                        + "    <deviceNo>1</deviceNo>\n"
                        + "    <Question>\n"
                        + "        <standardQuestionID>standardQuestionID1</standardQuestionID>\n"
                        + "    </Question>\n"
                        + "</EReport> ");

        var userToken = new ca.bc.gov.open.icon.ereporting.UserToken();

        var report = new Ereport();
        report.setCsNum("A");
        report.setDeviceNo("A");
        report.setEventID("A");
        report.setState("A");
        var question = new Question();
        question.setStandardQuestionID("A");
        question.setStandardText("A");
        question.setAdditionalText("A");
        var answer = new Answer();
        answer.setCode("A");
        answer.setDescription("A");
        question.getAnswer().add(answer);
        report.getQuestion().add(question);

        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");
        req.setUserTokenString("A");

        ResponseEntity<Ereport> responseEntity = new ResponseEntity<>(report, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Ereport>>any()))
                .thenReturn(responseEntity);

        var resp = reportingController.submitAnswers(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetAppointment()
            throws JsonProcessingException, JAXBException, UnsupportedEncodingException {
        var req = new GetAppointment();
        var appointment = new Appointment();

        appointment.setCsNum("A");
        appointment.setStartDate("A");
        appointment.setEndDate("A");
        appointment.setStartTime("A");
        appointment.setEndTime("A");

        req.setXMLString("A");

        var userToken = new ca.bc.gov.open.icon.ereporting.UserToken();

        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        req.setUserTokenString("A");

        ResponseEntity<Appointment> responseEntity =
                new ResponseEntity<>(appointment, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Appointment>>any()))
                .thenReturn(responseEntity);

        var resp = reportingController.getAppointment(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetQuestions() throws JsonProcessingException {
        var req = new GetQuestions();
        req.setXMLString("<EReport>\n" + "    <csNum>1</csNum>\n" + "</EReport> ");

        var report = new Ereport();
        report.setCsNum("A");
        report.setDeviceNo("A");
        report.setEventID("A");
        report.setState("A");
        var question = new Question();
        question.setStandardQuestionID("A");
        question.setStandardText("A");
        question.setAdditionalText("A");
        var answer = new Answer();
        answer.setCode("A");
        answer.setDescription("A");
        question.getAnswer().add(answer);
        report.getQuestion().add(question);

        req.setUserTokenString("A");

        ResponseEntity<Ereport> responseEntity = new ResponseEntity<>(report, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Ereport>>any()))
                .thenReturn(responseEntity);

        var resp = reportingController.getQuestions(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetStatus() throws JsonProcessingException {
        var req = new GetStatus();

        var status = new ca.bc.gov.open.icon.ereporting.Status();

        status.setEventId("A");
        status.setCsNum("A");
        status.setHasNextAppointment("A");
        status.setIsCurrentAppointment("A");
        status.setProfileEnabled("A");
        status.setAnswersSubmitted("A");
        status.setAnswersCorrect("A");

        req.setXMLString("A");

        var userToken = new ca.bc.gov.open.icon.ereporting.UserToken();

        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        req.setUserTokenString("A");

        ResponseEntity<ca.bc.gov.open.icon.ereporting.Status> responseEntity =
                new ResponseEntity<>(status, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<ca.bc.gov.open.icon.ereporting.Status>>any()))
                .thenReturn(responseEntity);

        var resp = reportingController.getStatus(req);
        Assertions.assertNotNull(resp);
    }
}
