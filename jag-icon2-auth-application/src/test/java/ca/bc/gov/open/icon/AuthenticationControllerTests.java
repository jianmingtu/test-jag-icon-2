package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.auth.*;
import ca.bc.gov.open.icon.controllers.AuthenticationController;
import ca.bc.gov.open.icon.utils.XMLUtilities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class AuthenticationControllerTests {

    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate;
    @Mock private AuthenticationController controller;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = Mockito.spy(new AuthenticationController(restTemplate, objectMapper));
    }

    @Test
    public void getPreAuthorizeClient() throws JsonProcessingException {

        PreAuthorizeClient preAuthorizeClient = new PreAuthorizeClient();
        preAuthorizeClient.setCsNum("A");
        preAuthorizeClient.setIsAllowed("A");

        GetPreAuthorizeClientDocument getPreAuthorizeClientDocument =
                new GetPreAuthorizeClientDocument();
        getPreAuthorizeClientDocument.setPreAuthorizeClient(preAuthorizeClient);

        // Set up to mock ords response
        ResponseEntity<PreAuthorizeClient> responseEntity =
                new ResponseEntity<>(preAuthorizeClient, HttpStatus.OK);
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<PreAuthorizeClient>>any()))
                .thenReturn(responseEntity);

        GetPreAuthorizeClient req = new GetPreAuthorizeClient();
        req.setXMLString(
                XMLUtilities.serializeXmlStr(
                        getPreAuthorizeClientDocument.getPreAuthorizeClient()));
        GetPreAuthorizeClientResponse resp = controller.getPreAuthorizeClient(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void getHasFunctionalAbility() throws JsonProcessingException {

        HasFunctionalAbility hasFunctionalAbility = new HasFunctionalAbility();
        FunctionalAbility functionalAbility = new FunctionalAbility();
        functionalAbility.setFunctionCd("A");
        functionalAbility.setServiceCd("A");
        hasFunctionalAbility.setFunctionalAbility(functionalAbility);

        UserToken userToken = new UserToken();
        userToken.setRemoteClientBrowserType("A");
        userToken.setRemoteClientHostName("A");
        userToken.setRemoteClientIPAddress("A");
        userToken.setUserIdentifier("A");
        userToken.setAuthoritativePartyIdentifier("A");
        userToken.setBiometricsSignature("A");
        userToken.setCSNumber("A");
        userToken.setSiteMinderSessionID("A");
        userToken.setSiteMinderTransactionID("A");

        GetHasFunctionalAbilityDocument getHasFunctionalAbilityDocument =
                new GetHasFunctionalAbilityDocument();
        getHasFunctionalAbilityDocument.setHasFunctionalAbility(hasFunctionalAbility);
        getHasFunctionalAbilityDocument.setUserToken(userToken);

        // Set up to mock ords response
        ResponseEntity<HasFunctionalAbility> responseEntity =
                new ResponseEntity<>(hasFunctionalAbility, HttpStatus.OK);
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<HasFunctionalAbility>>any()))
                .thenReturn(responseEntity);

        GetHasFunctionalAbility req = new GetHasFunctionalAbility();
        req.setXMLString(
                XMLUtilities.serializeXmlStr(
                        getHasFunctionalAbilityDocument.getHasFunctionalAbility()));
        req.setUserTokenString(
                XMLUtilities.serializeXmlStr(getHasFunctionalAbilityDocument.getUserToken()));
        var resp = controller.getHasFunctionalAbility(req);
        Assertions.assertNotNull(resp);
    }
}
