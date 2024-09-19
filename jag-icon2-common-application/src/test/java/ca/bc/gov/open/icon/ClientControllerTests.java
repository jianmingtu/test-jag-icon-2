package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.controllers.ClientController;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfo;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfoDocument;
import ca.bc.gov.open.icon.tombstone.GetTombStoneInfoResponse;
import ca.bc.gov.open.icon.tombstone.TombStoneInfo;
import ca.bc.gov.open.icon.trustaccount.*;
import ca.bc.gov.open.icon.trustaccount.Row;
import ca.bc.gov.open.icon.trustaccount.UserToken;
import ca.bc.gov.open.icon.utils.XMLUtilities;
import ca.bc.gov.open.icon.visitschedule.*;
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
public class ClientControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate;
    @Mock private ClientController controller;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = Mockito.spy(new ClientController(restTemplate, objectMapper));
    }

    @Test
    public void testGetTombStoneInfo() throws JsonProcessingException {
        TombStoneInfo tombStoneInfo = new TombStoneInfo();
        tombStoneInfo.setCsNum("A");
        tombStoneInfo.setBusinessRole("A");
        tombStoneInfo.setFirstName("A");
        tombStoneInfo.setLastName("A");
        tombStoneInfo.setLatestPhoto("A");
        tombStoneInfo.setUnreadMessageCount("A");

        GetTombStoneInfoDocument getTombStoneInfoDocument = new GetTombStoneInfoDocument();
        getTombStoneInfoDocument.setTombStoneInfo(tombStoneInfo);

        GetTombStoneInfo req = new GetTombStoneInfo();
        req.setXMLString(XMLUtilities.serializeXmlStr(getTombStoneInfoDocument.getTombStoneInfo()));

        // Set up to mock ords response
        ResponseEntity<TombStoneInfo> responseEntity =
                new ResponseEntity<>(tombStoneInfo, HttpStatus.OK);
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<TombStoneInfo>>any()))
                .thenReturn(responseEntity);

        ClientController clientController = new ClientController(restTemplate, objectMapper);
        GetTombStoneInfoResponse resp = clientController.getTombStoneInfo(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetTrustAccount() throws JsonProcessingException {
        TrustAccount trustAccount = new TrustAccount();
        trustAccount.setCsNum("A");
        trustAccount.setFundsAvailable("A");
        trustAccount.setTotalFunds("A");
        trustAccount.setFundsOnHold("A");

        Row row = new Row();
        row.setStart("A");
        row.setEnd("A");
        row.setTotal("A");
        trustAccount.setRow(row);

        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setComment("A");
        transactionDetails.setDate("A");
        transactionDetails.setDeposit("A");
        transactionDetails.setTotal("A");
        transactionDetails.setId("A");
        transactionDetails.setWithdrawal("A");
        transactionDetails.setOnHold("A");
        trustAccount.getTransactionDetails().add(transactionDetails);

        var userToken = new UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        GetTrustAccountDocument doc = new GetTrustAccountDocument();
        doc.setTrustAccount(trustAccount);
        doc.setUserToken(userToken);

        GetTrustAccount req = new GetTrustAccount();
        req.setXMLString(XMLUtilities.serializeXmlStr(doc.getTrustAccount()));
        req.setUserTokenString(XMLUtilities.serializeXmlStr(doc.getUserToken()));

        // Set up to mock ords response
        ResponseEntity<TrustAccount> responseEntity =
                new ResponseEntity<>(trustAccount, HttpStatus.OK);
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<TrustAccount>>any()))
                .thenReturn(responseEntity);

        GetTrustAccountResponse resp = controller.getTrustAccount(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetVisitSchedule() throws JsonProcessingException {
        VisitSchedule visitSchedule = new VisitSchedule();
        visitSchedule.setCsNum("A");

        var row = new ca.bc.gov.open.icon.visitschedule.Row();
        row.setStart("A");
        row.setEnd("A");
        row.setStart("A");
        visitSchedule.setRow(row);

        VisitScheduleDetails details = new VisitScheduleDetails();
        details.setDate("A");
        details.setStartTime("A");
        details.setEndTime("A");
        details.setWeekDay("A");
        visitSchedule.getVisitScheduleDetails().add(details);

        var userToken = new ca.bc.gov.open.icon.visitschedule.UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        GetVisitScheduleDocument doc = new GetVisitScheduleDocument();
        doc.setVisitSchedule(visitSchedule);
        doc.setUserToken(userToken);

        GetVisitSchedule req = new GetVisitSchedule();
        req.setXMLString(XMLUtilities.serializeXmlStr(doc.getVisitSchedule()));
        req.setUserTokenString(XMLUtilities.serializeXmlStr(doc.getUserToken()));

        // Set up to mock ords response
        ResponseEntity<VisitSchedule> responseEntity =
                new ResponseEntity<>(visitSchedule, HttpStatus.OK);
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<VisitSchedule>>any()))
                .thenReturn(responseEntity);

        GetVisitScheduleResponse resp = controller.getVisitSchedule(req);
        Assertions.assertNotNull(resp);
    }
}
