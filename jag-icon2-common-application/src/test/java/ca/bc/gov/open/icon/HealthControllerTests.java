package ca.bc.gov.open.icon;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.Base;
import ca.bc.gov.open.icon.audit.HealthServiceRequest;
import ca.bc.gov.open.icon.audit.HealthServiceRequestSubmitted;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.configuration.QueueConfig;
import ca.bc.gov.open.icon.controllers.*;
import ca.bc.gov.open.icon.hsr.*;
import ca.bc.gov.open.icon.hsrservice.*;
import ca.bc.gov.open.icon.models.HealthServicePub;
import ca.bc.gov.open.icon.utils.XMLUtilities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HealthControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private WebServiceTemplate webServiceTemplate;
    @Mock private RestTemplate restTemplate;

    @Qualifier("hsr-queue")
    private org.springframework.amqp.core.Queue hsrQueue;

    @Qualifier("ping-queue")
    private org.springframework.amqp.core.Queue pingQueue;

    @MockBean private RabbitTemplate rabbitTemplate;
    @MockBean private AmqpAdmin amqpAdmin;
    @Mock private QueueConfig queueConfig;
    @Mock private InformationController controller;
    @Mock private HealthController healthController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        controller = Mockito.spy(new InformationController(restTemplate, objectMapper));

        healthController =
                Mockito.spy(
                        new HealthController(
                                webServiceTemplate,
                                restTemplate,
                                objectMapper,
                                hsrQueue,
                                pingQueue,
                                rabbitTemplate,
                                amqpAdmin,
                                queueConfig));
    }

    @Test
    public void testHealthServiceRequestSubmitted() throws JsonProcessingException {
        var req = new HealthServiceRequestSubmitted();
        var healthServiceRequest = new HealthServiceRequest();
        var base = new Base();
        base.setSessionID("A");
        base.setCsNumber("A");
        base.setSessionID("A");
        healthServiceRequest.setBase(base);
        healthServiceRequest.setHealthServiceRequestID("A");
        healthServiceRequest.setServiceCD("A");
        healthServiceRequest.setFunctionCD("A");

        Status status = new Status();
        status.setSuccess(true);
        ResponseEntity<Status> responseEntity = new ResponseEntity<>(status, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Status>>any()))
                .thenReturn(responseEntity);

        var resp = healthController.healthServiceRequestSubmitted(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetHealthServiceRequestHistory() throws JsonProcessingException {
        var req = new GetHealthServiceRequestHistory();
        req.setXMLString(
                "<HealthService>\n"
                        + "    <csNum>1</csNum>\n"
                        + "    <Row>\n"
                        + "        <start>0</start>\n"
                        + "        <end>1</end>\n"
                        + "    </Row>\n"
                        + "</HealthService>");

        var userToken = new ca.bc.gov.open.icon.hsr.UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");
        req.setUserTokenString(XMLUtilities.serializeXmlStr(userToken));

        Map<String, String> out = new HashMap<>();
        out.put("isAllowed", "1");
        ResponseEntity<Map<String, String>> responseEntity =
                new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenReturn(responseEntity);

        // Set up to mock soap service response
        GetHealthServiceRequestSummaryResponse soapResp =
                new GetHealthServiceRequestSummaryResponse();
        var healthServiceRequestBundle = new HealthServiceRequestBundle();
        var arrayOfHealthServiceRequest = new ArrayOfHealthServiceRequest();
        var hsr = new ca.bc.gov.open.icon.hsrservice.HealthServiceRequest();
        hsr.setDetailsTxt("A");
        hsr.setSubmittedDtm("A");
        hsr.setId(1);
        arrayOfHealthServiceRequest.getRequests().add(hsr);
        healthServiceRequestBundle.setTotalRequestCount(1);
        healthServiceRequestBundle.setRequests(arrayOfHealthServiceRequest);
        soapResp.setGetHealthServiceRequestSummaryReturn(healthServiceRequestBundle);

        when(webServiceTemplate.marshalSendAndReceive(
                        anyString(), Mockito.any(GetHealthServiceRequestSummary.class)))
                .thenReturn(soapResp);

        var resp = healthController.getHealthServiceRequestHistory(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testPublishHSR() throws JsonProcessingException {
        var req = new PublishHSR();
        req.setXMLString(
                "<HealthService>\n"
                        + "    <csNum>1</csNum>\n"
                        + "    <HealthServiceRequest>\n"
                        + "        <hsrId>1</hsrId>\n"
                        + "        <pacID>1</pacID>\n"
                        + "        <location>1</location>\n"
                        + "        <requestDate></requestDate>\n"
                        + "        <healthRequest>Test!</healthRequest>\n"
                        + "    </HealthServiceRequest>\n"
                        + "</HealthService> ");

        List<HealthServicePub> healthServicePubs = new ArrayList();
        var healthServicePub = new HealthServicePub();
        healthServicePub.setCsNum("A");
        healthServicePub.setHsrId("A");
        healthServicePub.setLocation("A");
        healthServicePub.setRequestDate("A");
        healthServicePub.setHealthRequest("A");
        healthServicePub.setPacId("A");
        healthServicePub.setCsNum("A");
        healthServicePub.setHsrId("A");
        healthServicePub.setLocation("A");
        healthServicePub.setRequestDate("A");
        healthServicePub.setHealthRequest("A");
        healthServicePub.setPacId("A");
        healthServicePubs.add(healthServicePub);
        ResponseEntity<List<HealthServicePub>> responseEntities =
                new ResponseEntity<>(healthServicePubs, HttpStatus.OK);

        var userToken = new ca.bc.gov.open.icon.hsr.UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");
        req.setUserTokenString(XMLUtilities.serializeXmlStr(userToken));

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<List<HealthServicePub>>>any()))
                .thenReturn(responseEntities);

        // Set up to mock ords response
        ResponseEntity<HealthServicePub> responseEntity =
                new ResponseEntity<>(healthServicePub, HttpStatus.OK);
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<HealthServicePub>>any()))
                .thenReturn(responseEntity);

        SubmitHealthServiceRequestResponse submitHealthServiceRequestResponse =
                new SubmitHealthServiceRequestResponse();
        submitHealthServiceRequestResponse.setSubmitHealthServiceRequestReturn(0);
        when(webServiceTemplate.marshalSendAndReceive(
                        anyString(), Mockito.any(SubmitHealthServiceRequest.class)))
                .thenReturn(submitHealthServiceRequestResponse);
        var resp = healthController.publishHSR(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetHSRCount() throws JsonProcessingException {
        var req = new GetHSRCount();
        req.setXMLString(
                "<HealthServiceCount>\n" + "    <csNum>1</csNum>\n" + "</HealthServiceCount>");

        var userToken = new ca.bc.gov.open.icon.hsr.UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");
        req.setUserTokenString(XMLUtilities.serializeXmlStr(userToken));

        var hsrCount = new HealthServiceCount();
        hsrCount.setCount("1");
        hsrCount.setMax("1");
        hsrCount.setHsrId("A");
        hsrCount.setCsNum("1");
        hsrCount.setXmitOkay("A");
        ResponseEntity<HealthServiceCount> responseEntity =
                new ResponseEntity<>(hsrCount, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<HealthServiceCount>>any()))
                .thenReturn(responseEntity);

        var resp = healthController.getHSRCount(req);
        Assertions.assertNotNull(resp);
    }
}
