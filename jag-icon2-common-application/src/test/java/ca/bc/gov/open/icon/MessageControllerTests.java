package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.Base;
import ca.bc.gov.open.icon.audit.Message;
import ca.bc.gov.open.icon.audit.MessageAccessed;
import ca.bc.gov.open.icon.audit.Status;
import ca.bc.gov.open.icon.controllers.MessageController;
import ca.bc.gov.open.icon.ereporting.*;
import ca.bc.gov.open.icon.message.*;
import ca.bc.gov.open.icon.utils.XMLUtilities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MessageControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate;
    @Mock private MessageController messageController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        messageController = Mockito.spy(new MessageController(restTemplate, objectMapper));
    }

    @Test
    public void testMessageAccessed() throws JsonProcessingException {
        var req = new MessageAccessed();
        var Message = new Message();
        var base = new Base();
        base.setSessionID("A");
        base.setCsNumber("A");
        base.setSessionID("A");
        Message.setBase(base);
        Message.setEServiceCD("A");
        Message.setEServiceFuntionCD("A");

        var status = new Status();
        status.setSuccess(true);
        ResponseEntity<Status> responseEntity = new ResponseEntity<>(status, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Status>>any()))
                .thenReturn(responseEntity);

        var resp = messageController.messageAccessed(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetMessage() throws JsonProcessingException {
        var req = new GetMessage();

        var AppointmentMessage = new AppointmentMessage();
        AppointmentMessage.setText("A");
        AppointmentMessage.setCsNum("A");
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

        var appointmentMessage = new AppointmentMessage();
        appointmentMessage.setCsNum("A");
        appointmentMessage.setText("A");
        ResponseEntity<AppointmentMessage> responseEntity =
                new ResponseEntity<>(appointmentMessage, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<AppointmentMessage>>any()))
                .thenReturn(responseEntity);
        var resp = messageController.getMessage(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testSetMessageDate() throws JsonProcessingException {
        var req = new SetMessageDate();
        req.setXMLString(
                "<AppointmentMessage>\n" + "    <csNum>1</csNum>\n" + "</AppointmentMessage> ");

        var AppointmentMessage = new AppointmentMessage();
        AppointmentMessage.setText("A");
        AppointmentMessage.setCsNum("A");

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

        var appointmentMessage = new AppointmentMessage();
        ResponseEntity<AppointmentMessage> responseEntity =
                new ResponseEntity<>(appointmentMessage, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<AppointmentMessage>>any()))
                .thenReturn(responseEntity);

        var resp = messageController.setMessageDate(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testSetMessageDetails() throws JsonProcessingException {
        Messages messages = new Messages();
        messages.setCsNum("A");
        messages.setUnreadMessageCount("A");
        Row row = new Row();
        row.setStart("1");
        row.setEnd("2");
        row.setTotal("3");
        messages.setRow(row);
        MessageDetails messageDetails = new MessageDetails();
        messageDetails.setId("A");
        messageDetails.setUnread("A");
        messageDetails.setTimestamp("A");
        MessageType messageType = new MessageType();
        messageType.setCode("A");
        messageType.setDescription("A");
        messageDetails.setMessageType(messageType);
        messageDetails.setText("A");
        Sender Sender = new Sender();

        Relationships relationship = new Relationships();
        relationship.setCode("A");
        relationship.setDescription("A");
        Application Application = new Application();
        Application.setCode("A");
        Application.setDescription("A");
        Sender.setApplication(Application);
        Individual individual = new Individual();
        individual.setFirstName("A");
        individual.setLastName("A");
        individual.getRelationships().add(relationship);
        Sender.setIndividual(individual);
        messageDetails.setSender(Sender);
        messageDetails.setHasDisclosureSet("A");
        messages.getMessageDetails().add(messageDetails);

        ca.bc.gov.open.icon.message.UserToken userToken =
                new ca.bc.gov.open.icon.message.UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        SetMessageDetails req = new SetMessageDetails();
        SetMessageDetailsDocument setMessageDetailsDocument = new SetMessageDetailsDocument();
        setMessageDetailsDocument.setMessages(messages);
        setMessageDetailsDocument.setUserToken(userToken);
        req.setXMLString(XMLUtilities.serializeXmlStr(setMessageDetailsDocument.getMessages()));
        req.setUserTokenString(
                XMLUtilities.serializeXmlStr(setMessageDetailsDocument.getUserToken()));

        ResponseEntity<Messages> responseEntity = new ResponseEntity<>(messages, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Messages>>any()))
                .thenReturn(responseEntity);

        var resp = messageController.setMessageDetails(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetMessages() throws JsonProcessingException {
        Messages messages = new Messages();
        messages.setCsNum("A");
        messages.setUnreadMessageCount("A");
        Row row = new Row();
        row.setStart("1");
        row.setEnd("2");
        row.setTotal("3");
        MessageDetails messageDetails = new MessageDetails();
        messageDetails.setId("A");
        messageDetails.setUnread("A");
        messageDetails.setTimestamp("A");
        MessageType messageType = new MessageType();
        messageType.setCode("A");
        messageType.setDescription("A");
        messageDetails.setMessageType(messageType);
        messageDetails.setText("A");
        Sender Sender = new Sender();
        Relationships relationships = new Relationships();
        relationships.setCode("A");
        relationships.setDescription("A");
        Application application = new Application();
        application.setCode("Application");
        application.setDescription("A");
        Sender.setApplication(application);
        Individual individual = new Individual();
        individual.setFirstName("A");
        individual.setLastName("A");
        individual.getRelationships().add(relationships);
        Sender.setIndividual(individual);
        messageDetails.setSender(Sender);
        messageDetails.setHasDisclosureSet("A");
        messages.getMessageDetails().add(messageDetails);

        ca.bc.gov.open.icon.message.UserToken userToken =
                new ca.bc.gov.open.icon.message.UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        GetMessages req = new GetMessages();
        GetMessagesDocument getMessagesDocument = new GetMessagesDocument();
        getMessagesDocument.setMessages(messages);
        getMessagesDocument.setUserToken(userToken);
        req.setXMLString(XMLUtilities.serializeXmlStr(getMessagesDocument.getMessages()));
        req.setUserTokenString(XMLUtilities.serializeXmlStr(getMessagesDocument.getUserToken()));

        ResponseEntity<Messages> responseEntity = new ResponseEntity<>(messages, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Messages>>any()))
                .thenReturn(responseEntity);

        var resp = messageController.getMessages(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetMessageDetails() throws JsonProcessingException {
        Messages messages = new Messages();
        messages.setCsNum("A");
        messages.setUnreadMessageCount("A");
        Row row = new Row();
        row.setStart("1");
        row.setEnd("2");
        row.setTotal("3");
        messages.setRow(row);
        MessageDetails messageDetails = new MessageDetails();
        messageDetails.setId("A");
        messageDetails.setUnread("A");
        messageDetails.setTimestamp("A");
        MessageType messageType = new MessageType();
        messageType.setCode("A");
        messageType.setDescription("A");
        messageDetails.setMessageType(messageType);
        messageDetails.setText("A");
        Sender Sender = new Sender();
        Relationships relationships = new Relationships();
        relationships.setCode("A");
        relationships.setDescription("A");
        Application application = new Application();
        application.setCode("Application");
        application.setDescription("A");
        Sender.setApplication(application);
        Individual individual = new Individual();
        individual.setFirstName("A");
        individual.setLastName("A");
        individual.getRelationships().add(relationships);
        Sender.setIndividual(individual);
        messageDetails.setSender(Sender);
        messageDetails.setHasDisclosureSet("A");
        messages.getMessageDetails().add(messageDetails);

        ca.bc.gov.open.icon.message.UserToken userToken =
                new ca.bc.gov.open.icon.message.UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        GetMessageDetails req = new GetMessageDetails();
        GetMessagesDocument getMessagesDocument = new GetMessagesDocument();
        getMessagesDocument.setMessages(messages);
        getMessagesDocument.setUserToken(userToken);
        req.setXMLString(XMLUtilities.serializeXmlStr(getMessagesDocument.getMessages()));
        req.setUserTokenString(XMLUtilities.serializeXmlStr(getMessagesDocument.getUserToken()));

        ResponseEntity<Messages> responseEntity = new ResponseEntity<>(messages, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Messages>>any()))
                .thenReturn(responseEntity);

        var resp = messageController.getMessageDetails(req);
        Assertions.assertNotNull(resp);
    }
}
