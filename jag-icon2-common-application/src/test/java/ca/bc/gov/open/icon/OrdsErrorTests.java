package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.configuration.QueueConfig;
import ca.bc.gov.open.icon.controllers.*;
import ca.bc.gov.open.icon.ereporting.*;
import ca.bc.gov.open.icon.error.SetErrorMessage;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.exceptions.ServiceFaultException;
import ca.bc.gov.open.icon.hsr.GetHSRCount;
import ca.bc.gov.open.icon.hsr.GetHealthServiceRequestHistory;
import ca.bc.gov.open.icon.hsr.PublishHSR;
import ca.bc.gov.open.icon.hsr.PublishHSRDocument;
import ca.bc.gov.open.icon.message.GetMessageDetails;
import ca.bc.gov.open.icon.message.GetMessages;
import ca.bc.gov.open.icon.message.SetMessageDetails;
import ca.bc.gov.open.icon.myinfo.*;
import ca.bc.gov.open.icon.myinfo.GetLocations;
import ca.bc.gov.open.icon.packageinfo.GetPackageInfo;
import ca.bc.gov.open.icon.session.GetSessionParameters;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfo;
import ca.bc.gov.open.icon.trustaccount.GetTrustAccount;
import ca.bc.gov.open.icon.visitschedule.GetVisitSchedule;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrdsErrorTests {
    @Mock private WebServiceTemplate webServiceTemplate;
    @Mock private RestTemplate restTemplate;
    @Mock private ObjectMapper objectMapper;
    @Mock private QueueConfig queueConfig;
    @Mock private RabbitTemplate rabbitTemplate;
    @Mock private AmqpAdmin amqpAdmin;

    @Qualifier("hsr-queue")
    @Mock
    private org.springframework.amqp.core.Queue hsrQueue;

    @Qualifier("ping-queue")
    @Mock
    private org.springframework.amqp.core.Queue pingQueue;

    @Mock private InformationController informationController;
    @Mock private AuditController auditController;
    @Mock private AuthenticationController authenticationController;
    @Mock private MessageController messageController;
    @Mock private RecordController recordController;
    @Mock private ReportingController reportingController;
    @Mock private HealthController healthController;
    @Mock private ClientController clientController;
    @Mock private ErrorHandlingController errorHandlingController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        informationController = Mockito.spy(new InformationController(restTemplate, objectMapper));
        auditController = Mockito.spy(new AuditController(restTemplate, objectMapper));
        authenticationController =
                Mockito.spy(new AuthenticationController(restTemplate, objectMapper));
        messageController = Mockito.spy(new MessageController(restTemplate, objectMapper));
        recordController = Mockito.spy(new RecordController(restTemplate, objectMapper));
        reportingController = Mockito.spy(new ReportingController(restTemplate, objectMapper));
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
        clientController = Mockito.spy(new ClientController(restTemplate, objectMapper));
        errorHandlingController =
                Mockito.spy(new ErrorHandlingController(restTemplate, objectMapper));
    }

    /*
        AuditController
    */
    @Test
    public void testEServiceAccessedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.eServiceAccessed(new EServiceAccessed()));
    }

    @Test
    public void testHomeScreenAccessedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.homeScreenAccessed(new HomeScreenAccessed()));
    }

    @Test
    public void testSessionTimeoutExecutedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.sessionTimeoutExecuted(new SessionTimeoutExecuted()));
    }

    @Test
    public void testEServiceFunctionAccessedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.eServiceFunctionAccessed(new EServiceFunctionAccessed()));
    }

    @Test
    public void testGetPackageInfoFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> auditController.getPackageInfo(new GetPackageInfo()));
    }

    @Test
    public void testGetSessionParametersFail() {
        GetSessionParameters getSessionParameters = new GetSessionParameters();
        getSessionParameters.setXMLString("A");
        Assertions.assertThrows(
                ORDSException.class,
                () -> auditController.getSessionParameters(getSessionParameters));
    }

    /*
        AuthenticationController
    */
    @Test
    public void testReauthenticationFailedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        authenticationController.reauthenticationFailed(
                                new ReauthenticationFailed()));
    }

    @Test
    public void testReauthenticationSucceededFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        authenticationController.reauthenticationSucceeded(
                                new ReauthenticationSucceeded()));
    }

    @Test
    public void testLogoutExecutedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> authenticationController.logoutExecuted(new LogoutExcecuted()));
    }

    @Test
    public void testIdleTimeoutExecutedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> authenticationController.idleTimeoutExecuted(new IdleTimeoutExecuted()));
    }

    @Test
    public void testPrimaryAuthenticationCompletedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        authenticationController.primaryAuthenticationCompleted(
                                new PrimaryAuthenticationCompleted()));
    }
    /*
        ClientController
    */

    @Test
    public void testGetTombStoneInfoFail() {
        GetTombStoneInfo getTombStoneInfo = new GetTombStoneInfo();
        getTombStoneInfo.setXMLString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> clientController.getTombStoneInfo(getTombStoneInfo));
    }

    @Test
    public void testGetTrustAccountFail() {
        GetTrustAccount getTrustAccount = new GetTrustAccount();
        getTrustAccount.setXMLString("A");
        getTrustAccount.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> clientController.getTrustAccount(getTrustAccount));
    }

    @Test
    public void testGetVisitScheduleFail() {
        GetVisitSchedule getVisitSchedule = new GetVisitSchedule();
        getVisitSchedule.setUserTokenString("A");
        getVisitSchedule.setXMLString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> clientController.getVisitSchedule(getVisitSchedule));
    }

    /*
        ErrorHandlingController
    */
    @Test
    public void testSetErrorMessageFail() {
        when(restTemplate.exchange(
                        Mockito.anyString(),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenThrow(new RestClientException("BAD"));

        Assertions.assertThrows(
                ServiceFaultException.class,
                () -> errorHandlingController.setErrorMessage(new SetErrorMessage()));
    }

    /*
        HealthController
    */
    @Test
    public void testHealthServiceRequestSubmittedFail() {

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        healthController.healthServiceRequestSubmitted(
                                new HealthServiceRequestSubmitted()));
    }

    @Test
    public void testGetHealthServiceRequestHistoryFail() {
        GetHealthServiceRequestHistory getHealthServiceRequestHistory =
                new GetHealthServiceRequestHistory();
        getHealthServiceRequestHistory.setXMLString("A");
        getHealthServiceRequestHistory.setUserTokenString("A");

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        healthController.getHealthServiceRequestHistory(
                                getHealthServiceRequestHistory));
    }

    @Test
    public void testPublishHSROrdsFail() {
        PublishHSR publishHSR = new PublishHSR();
        publishHSR.setXMLString("A");
        publishHSR.setUserTokenString("A");

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<PublishHSRDocument>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenThrow(new ORDSException());
        Assertions.assertThrows(ORDSException.class, () -> healthController.publishHSR(publishHSR));
    }

    @Test
    public void testPublishHSRFail() {
        PublishHSR publishHSR = new PublishHSR();
        publishHSR.setXMLString("A");
        publishHSR.setUserTokenString("A");

        Assertions.assertThrows(
                ServiceFaultException.class, () -> healthController.publishHSR(publishHSR));
    }

    @Test
    public void testGetHSRCountFail() {
        GetHSRCount getHSRCount = new GetHSRCount();
        getHSRCount.setXMLString("A");
        getHSRCount.setUserTokenString("A");

        Assertions.assertThrows(
                ORDSException.class, () -> healthController.getHSRCount(getHSRCount));
    }

    /*
        InformationController
    */

    @Test
    public void testGetOrdersFail() {
        GetOrders getOrders = new GetOrders();
        getOrders.setXMLString("A");
        getOrders.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> informationController.getOrders(getOrders));
    }

    @Test
    public void testGetProgramsFail() {
        GetPrograms getPrograms = new GetPrograms();
        getPrograms.setXMLString("A");
        getPrograms.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> informationController.getPrograms(getPrograms));
    }

    @Test
    public void testGetLocationsFail() {
        GetLocations getLocations = new GetLocations();
        getLocations.setXMLString("A");
        getLocations.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> informationController.getLocations(getLocations));
    }

    @Test
    public void testGetConditionsFail() {
        GetConditions getConditions = new GetConditions();
        getConditions.setXMLString("A");
        getConditions.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> informationController.getConditions(getConditions));
    }

    @Test
    public void testGetOrdersConditionsFail() {
        GetOrdersConditions getOrdersConditions = new GetOrdersConditions();
        getOrdersConditions.setXMLString("A");
        getOrdersConditions.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class,
                () -> informationController.getOrdersConditions(getOrdersConditions));
    }

    @Test
    public void testGetDatesFail() {
        GetDates getDates = new GetDates();
        getDates.setXMLString("A");
        getDates.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> informationController.getDates(getDates));
    }

    @Test
    public void testGetClientHistoryFail() {
        GetClientHistory getClientHistory = new GetClientHistory();
        getClientHistory.setXMLString("A");
        getClientHistory.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class,
                () -> informationController.getClientHistory(getClientHistory));
    }

    /*
        MessageController
    */

    @Test
    public void testMessageAccessedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> messageController.messageAccessed(new MessageAccessed()));
    }

    @Test
    public void testGetMessageFail() {
        GetMessage getMessage = new GetMessage();
        getMessage.setXMLString("A");
        getMessage.setUserTokenString("A");

        Assertions.assertThrows(
                ORDSException.class, () -> messageController.getMessage(getMessage));
    }

    @Test
    public void testSetMessageDateFail() {
        SetMessageDate setMessageDate = new SetMessageDate();
        setMessageDate.setXMLString("A");
        setMessageDate.setUserTokenString("A");

        Map<String, String> out = new HashMap<>();
        ResponseEntity<Map<String, String>> responseEntity =
                new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenReturn(responseEntity);

        Assertions.assertThrows(
                ORDSException.class, () -> messageController.setMessageDate(setMessageDate));
    }

    @Test
    public void testSetMessageDetailsFail() {
        SetMessageDetails setMessageDetails = new SetMessageDetails();
        setMessageDetails.setXMLString("A");
        setMessageDetails.setUserTokenString("A");

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenThrow(new ORDSException());

        Assertions.assertThrows(
                ORDSException.class, () -> messageController.setMessageDetails(setMessageDetails));
    }

    @Test
    public void testGetMessagesFail() {
        GetMessages getMessages = new GetMessages();
        getMessages.setXMLString("A");
        getMessages.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> messageController.getMessages(getMessages));
    }

    @Test
    public void testGetMessageDetailsFail() {
        GetMessageDetails getMessageDetails = new GetMessageDetails();
        getMessageDetails.setXMLString("A");
        getMessageDetails.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> messageController.getMessageDetails(getMessageDetails));
    }

    /*
        RecordController
    */
    @Test
    public void testRecordCompletedFail() {
        RecordCompleted recordCompleted = new RecordCompleted();
        recordCompleted.setXMLString("A");

        when(restTemplate.exchange(
                        Mockito.anyString(),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenThrow(new RestClientException("BAD"));

        Assertions.assertThrows(
                ServiceFaultException.class,
                () -> recordController.recordCompleted(recordCompleted));
    }

    @Test
    public void testRecordExceptionFail() {
        RecordException recordException = new RecordException();
        recordException.setXMLString("A");

        when(restTemplate.exchange(
                        Mockito.anyString(),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenThrow(new RestClientException("BAD"));

        Assertions.assertThrows(
                ServiceFaultException.class,
                () -> recordController.recordException(recordException));
    }

    /*
        ReportingController
    */
    @Test
    public void testEReportAnswersSubmittedFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> reportingController.eReportAnswersSubmitted(new EReportAnswersSubmitted()));
    }

    @Test
    public void testGetReportingCmpltInstructionFail() {
        GetReportingCmpltInstruction getReportingCmpltInstruction =
                new GetReportingCmpltInstruction();
        getReportingCmpltInstruction.setXMLString("A");
        getReportingCmpltInstruction.setUserTokenString("A");

        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        reportingController.getReportingCmpltInstruction(
                                getReportingCmpltInstruction));
    }

    @Test
    public void testGetLocationsResponseFail() {
        ca.bc.gov.open.icon.ereporting.GetLocations getLocations =
                new ca.bc.gov.open.icon.ereporting.GetLocations();
        getLocations.setXMLString("A");
        getLocations.setUserTokenString("A");

        Assertions.assertThrows(
                ORDSException.class, () -> reportingController.getLocationsResponse(getLocations));
    }

    @Test
    public void testSubmitAnswersFail() {
        SubmitAnswers submitAnswers = new SubmitAnswers();
        submitAnswers.setXMLString(
                "<EReport>\n"
                        + "    <csNum>1</csNum>\n"
                        + "    <eventId>1</eventId>\n"
                        + "    <pacID>1</pacID>\n"
                        + "    <deviceNo>1</deviceNo>\n"
                        + "    <Question>\n"
                        + "        <QuestionId>0</QuestionId>\n"
                        + "        <standardQuestionID>standardQuestionID1</standardQuestionID>\n"
                        + "    </Question>\n"
                        + "</EReport> ");

        submitAnswers.setUserTokenString("A");

        Map<String, String> out = new HashMap<>();
        ResponseEntity<Map<String, String>> responseEntity =
                new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenThrow(ORDSException.class);

        Assertions.assertThrows(
                ORDSException.class, () -> reportingController.submitAnswers(submitAnswers));
    }

    @Test
    public void testGetAppointmentFail() {
        GetAppointment getAppointment = new GetAppointment();
        getAppointment.setXMLString("A");
        getAppointment.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> reportingController.getAppointment(getAppointment));
    }

    @Test
    public void testGetQuestionsFail() {
        GetQuestions getQuestions = new GetQuestions();
        getQuestions.setXMLString("A");
        getQuestions.setUserTokenString("A");

        Assertions.assertThrows(
                ORDSException.class, () -> reportingController.getQuestions(getQuestions));
    }

    @Test
    public void testGetStatusFail() {
        ca.bc.gov.open.icon.ereporting.GetStatus getStatus =
                new ca.bc.gov.open.icon.ereporting.GetStatus();
        getStatus.setXMLString("A");
        getStatus.setUserTokenString("A");

        Assertions.assertThrows(
                ORDSException.class, () -> reportingController.getStatus(getStatus));
    }
}
