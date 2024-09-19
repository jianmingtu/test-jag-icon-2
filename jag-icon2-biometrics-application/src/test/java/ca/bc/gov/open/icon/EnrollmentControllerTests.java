package ca.bc.gov.open.icon;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.bcs.IssuanceToken;
import ca.bc.gov.open.icon.bcs.StartEnrollmentResponse2;
import ca.bc.gov.open.icon.biometrics.StartEnrollment;
import ca.bc.gov.open.icon.controllers.EnrollmentController;
import ca.bc.gov.open.icon.iis.*;
import ca.bc.gov.open.icon.ips.*;
import ca.bc.gov.open.icon.ips.ResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EnrollmentControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate;
    @Mock private WebServiceTemplate soapTemplate;
    @Mock private EnrollmentController enrollmentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        enrollmentController =
                Mockito.spy(new EnrollmentController(soapTemplate, objectMapper, restTemplate));
    }

    @Test
    public void testStartEnrollment() throws JsonProcessingException {
        var req = new StartEnrollment();
        req.setCsNum("A");
        req.setRequestorUserId("A");
        req.setRequestorType("LDB");

        Map<String, String> out = new HashMap<>();
        out.put("andid", "1");
        ResponseEntity<Map<String, String>> responseEntity =
                new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<ParameterizedTypeReference<Map<String, String>>>any()))
                .thenReturn(responseEntity);

        //      Set up IIS Request
        RegisterIndividual iisReq = new RegisterIndividual();
        RegisterIndividualRequest issReqInner = new RegisterIndividualRequest();
        issReqInner.setOnlineServiceId("A");
        issReqInner.setRequesterUserId("A");
        issReqInner.setRequesterAccountTypeCode(
                ca.bc.gov.open.icon.iis.BCeIDAccountTypeCode.fromValue("Individual"));
        issReqInner.setRequesterUserId("A");
        issReqInner.setRequesterUserGuid("A");
        issReqInner.setAccountType("A");
        issReqInner.setSurname("A");
        issReqInner.setFirstGivenName("A");
        issReqInner.setMiddleName1("A");
        issReqInner.setMiddleName2("A");
        issReqInner.setKnownAs("A");
        issReqInner.setBirthDate(Instant.now());
        issReqInner.setTelephone("A");
        issReqInner.setEmail("A");
        var Address = new Address();
        Address.setLine1("A");
        Address.setLine2("A");
        Address.setPostalCode("A");
        Address.setCity("A");
        Address.setProvince("A");
        Address.setCountry("A");
        issReqInner.setPostalAddress(Address);
        iisReq.setRequest(issReqInner);

        var soapResp = new RegisterIndividualResponse();
        var registerIndividualResponse2 = new RegisterIndividualResponse2();
        soapResp.setRegisterIndividualResult(registerIndividualResponse2);
        registerIndividualResponse2.setCode(ca.bc.gov.open.icon.iis.ResponseCode.SUCCESS);
        registerIndividualResponse2.setIdRef("A");

        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(RegisterIndividual.class)))
                .thenReturn(soapResp);

        // Set up to mock soap service response
        var soapResp1 = new LinkResponse();
        var LinkResponse2 = new LinkResponse2();
        soapResp1.setLinkResult(LinkResponse2);
        LinkResponse2.setCode(ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(Link.class)))
                .thenReturn(soapResp1);

        // Set up to mock soap service response
        var soapResp2 = new GetIdRefResponse();
        var GetIdRefResponse2 = new GetIdRefResponse2();
        soapResp2.setGetIdRefResult(GetIdRefResponse2);
        GetIdRefResponse2.setCode(ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(GetIdRef.class)))
                .thenReturn(soapResp2);

        // Set up to mock soap service response
        var soapResp3 = new ca.bc.gov.open.icon.bcs.StartEnrollmentResponse();
        var startEnrollmentResponse2 = new StartEnrollmentResponse2();
        var issuanceToken = new IssuanceToken();
        issuanceToken.setIssuanceID("A");
        issuanceToken.setEnrollmentURL("A");
        issuanceToken.setExpiry("A");
        startEnrollmentResponse2.setIssuance(issuanceToken);
        soapResp3.setStartEnrollmentResult(startEnrollmentResponse2);
        startEnrollmentResponse2.setCode(ca.bc.gov.open.icon.bcs.ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(
                        anyString(), Mockito.any(ca.bc.gov.open.icon.bcs.StartEnrollment.class)))
                .thenReturn(soapResp3);

        var resp = enrollmentController.startEnrollment(req);
        Assertions.assertNotNull(resp);
    }
}
