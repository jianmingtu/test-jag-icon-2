package ca.bc.gov.open.icon;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.bcs.*;
import ca.bc.gov.open.icon.biometrics.Move;
import ca.bc.gov.open.icon.biometrics.Remove;
import ca.bc.gov.open.icon.biometrics.RemoveIdentity;
import ca.bc.gov.open.icon.biometrics.RemoveTemplate;
import ca.bc.gov.open.icon.controllers.RemovalController;
import ca.bc.gov.open.icon.iis.*;
import ca.bc.gov.open.icon.iis.BCeIDAccountTypeCode;
import ca.bc.gov.open.icon.iis.ResponseCode;
import ca.bc.gov.open.icon.ips.*;
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
public class RemovalControllerTests {

    @Mock private ObjectMapper objectMapper;
    @Mock private WebServiceTemplate soapTemplate;
    @Mock private RestTemplate restTemplate;
    @Mock private RemovalController removalController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        removalController =
                Mockito.spy(new RemovalController(soapTemplate, objectMapper, restTemplate));
    }

    @Test
    public void testMove() throws JsonProcessingException {
        var req = new Move();
        req.setCsNumTo("A");
        req.setCsNumFrom("A");
        req.setRequestorUserId("A");
        req.setRequestorType("LDB");
        req.setCredentialRefFrom("A");
        req.setIssuanceID("A");
        req.setEnrollmentURL("A");
        req.setExpiry("A");

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
        issReqInner.setRequesterAccountTypeCode(BCeIDAccountTypeCode.fromValue("Individual"));
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
        LinkResponse2.setCode(ca.bc.gov.open.icon.ips.ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(Link.class)))
                .thenReturn(soapResp1);

        // Set up to mock soap service response
        var soapResp2 = new RebindCredentialResponse();
        var rebindCredentialResponse2 = new RebindCredentialResponse2();
        soapResp2.setRebindCredentialResult(rebindCredentialResponse2);
        rebindCredentialResponse2.setCode(ca.bc.gov.open.icon.bcs.ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(RebindCredential.class)))
                .thenReturn(soapResp2);

        // Set up to mock soap service response
        var soapResp3 = new RemoveIndividualResponse();
        var removeIndividualResponse2 = new RemoveIndividualResponse2();
        soapResp3.setRemoveIndividualResult(removeIndividualResponse2);
        removeIndividualResponse2.setCode(ca.bc.gov.open.icon.iis.ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(RemoveIndividual.class)))
                .thenReturn(soapResp3);

        // Set up to mock soap service response
        var soapResp4 = new UnlinkResponse();
        var unlinkResponse2 = new UnlinkResponse2();
        soapResp4.setUnlinkResult(unlinkResponse2);
        unlinkResponse2.setCode(ca.bc.gov.open.icon.ips.ResponseCode.SUCCESS);
        soapResp3.setRemoveIndividualResult(removeIndividualResponse2);
        removeIndividualResponse2.setCode(ca.bc.gov.open.icon.iis.ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(Unlink.class)))
                .thenReturn(soapResp4);

        // Set up to mock soap service response
        var common = new GetIdRefResponse();
        var getIdRefResponse2 = new GetIdRefResponse2();
        getIdRefResponse2.setIdRef("A");
        getIdRefResponse2.setCode(ca.bc.gov.open.icon.ips.ResponseCode.SUCCESS);
        common.setGetIdRefResult(getIdRefResponse2);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(GetIdRef.class)))
                .thenReturn(common);

        var resp = removalController.move(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testRemove() throws JsonProcessingException {
        var req = new Remove();
        req.setCredentialRef("A");
        req.setExpiry("A");
        req.setRegistrar("A");
        req.setIssuanceID("A");

        // Set up to mock soap service response
        var soapResp = new DestroyCredentialResponse();
        var destroyCredentialResponse2 = new DestroyCredentialResponse2();
        soapResp.setDestroyCredentialResult(destroyCredentialResponse2);
        destroyCredentialResponse2.setCode(ca.bc.gov.open.icon.bcs.ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(DestroyCredential.class)))
                .thenReturn(soapResp);

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

        // Set up to mock soap service response
        var soapResp1 = new RemoveIndividualResponse();
        var removeIndividualResponse2 = new RemoveIndividualResponse2();
        soapResp1.setRemoveIndividualResult(removeIndividualResponse2);
        removeIndividualResponse2.setCode(ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(RemoveIndividual.class)))
                .thenReturn(soapResp1);

        // Set up to mock soap service response
        var soapResp2 = new UnlinkResponse();
        var unlinkResponse2 = new UnlinkResponse2();
        soapResp2.setUnlinkResult(unlinkResponse2);
        unlinkResponse2.setCode(ca.bc.gov.open.icon.ips.ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(Unlink.class)))
                .thenReturn(soapResp2);

        // Set up to mock soap service response
        var common = new GetIdRefResponse();
        var getIdRefResponse2 = new GetIdRefResponse2();
        getIdRefResponse2.setIdRef("A");
        getIdRefResponse2.setCode(ca.bc.gov.open.icon.ips.ResponseCode.SUCCESS);
        common.setGetIdRefResult(getIdRefResponse2);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(GetIdRef.class)))
                .thenReturn(common);

        var resp = removalController.remove(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testRemoveIdentity() throws JsonProcessingException {
        var req = new RemoveIdentity();
        req.setCsNum("A");
        req.setExpiry("A");
        req.setEnrollmentURL("A");
        req.setIssuanceID("A");
        req.setRequestorUserId("A");
        req.setRequestorType("Business");

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

        // Set up to mock soap service response
        var soapResp = new RemoveIndividualResponse();
        var demoveIndividualResponse2 = new RemoveIndividualResponse2();
        soapResp.setRemoveIndividualResult(demoveIndividualResponse2);
        demoveIndividualResponse2.setCode(ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(RemoveIndividual.class)))
                .thenReturn(soapResp);

        // Set up to mock soap service response
        var soapResp2 = new UnlinkResponse();
        var unlinkResponse2 = new UnlinkResponse2();
        soapResp2.setUnlinkResult(unlinkResponse2);
        unlinkResponse2.setCode(ca.bc.gov.open.icon.ips.ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(Unlink.class)))
                .thenReturn(soapResp2);

        // Set up to mock soap service response
        var common = new GetIdRefResponse();
        var getIdRefResponse2 = new GetIdRefResponse2();
        getIdRefResponse2.setIdRef("A");
        getIdRefResponse2.setCode(ca.bc.gov.open.icon.ips.ResponseCode.SUCCESS);
        common.setGetIdRefResult(getIdRefResponse2);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(GetIdRef.class)))
                .thenReturn(common);

        var resp = removalController.removeIdentity(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testRemoveTemplate() throws JsonProcessingException {
        var req = new RemoveTemplate();
        req.setCredentialRef("A");
        req.setExpiry("A");
        req.setEnrollmentURL("A");
        req.setIssuanceID("A");
        req.setRequestorUserId("A");
        req.setRequestorType("CORNET");

        // Set up to mock soap service response
        var soapResp = new DestroyCredentialResponse();
        var destroyCredentialResponse2 = new DestroyCredentialResponse2();
        soapResp.setDestroyCredentialResult(destroyCredentialResponse2);
        destroyCredentialResponse2.setCode(ca.bc.gov.open.icon.bcs.ResponseCode.SUCCESS);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(DestroyCredential.class)))
                .thenReturn(soapResp);

        // Set up to mock soap service response
        var common = new GetIdRefResponse();
        var getIdRefResponse2 = new GetIdRefResponse2();
        getIdRefResponse2.setIdRef("A");
        getIdRefResponse2.setCode(ca.bc.gov.open.icon.ips.ResponseCode.SUCCESS);
        common.setGetIdRefResult(getIdRefResponse2);
        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(GetIdRef.class)))
                .thenReturn(common);

        var resp = removalController.removeTemplate(req);
        Assertions.assertNotNull(resp);
    }
}
