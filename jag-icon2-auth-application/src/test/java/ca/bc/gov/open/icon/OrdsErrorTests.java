package ca.bc.gov.open.icon;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.auth.GetDeviceInfo;
import ca.bc.gov.open.icon.auth.GetHasFunctionalAbility;
import ca.bc.gov.open.icon.auth.GetPreAuthorizeClient;
import ca.bc.gov.open.icon.auth.GetUserInfo;
import ca.bc.gov.open.icon.controllers.AuthenticationController;
import ca.bc.gov.open.icon.controllers.InformationController;
import ca.bc.gov.open.icon.ereporting.*;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrdsErrorTests {

    @Mock private WebServiceTemplate webServiceTemplate;
    @Mock private RestTemplate restTemplate;
    @Mock private ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;

    @Mock private InformationController informationController;
    @Mock private AuthenticationController authenticationController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        informationController = Mockito.spy(new InformationController(restTemplate, objectMapper));
        authenticationController =
                Mockito.spy(new AuthenticationController(restTemplate, objectMapper));
    }

    @Test
    public void testGetPreAuthorizeClientFail() {
        GetPreAuthorizeClient getPreAuthorizeClient = new GetPreAuthorizeClient();
        getPreAuthorizeClient.setXMLString("A");
        Assertions.assertThrows(
                ORDSException.class,
                () -> authenticationController.getPreAuthorizeClient(getPreAuthorizeClient));
    }

    @Test
    public void testGetHasFunctionalAbilityFail() {
        GetHasFunctionalAbility getHasFunctionalAbility = new GetHasFunctionalAbility();
        getHasFunctionalAbility.setXMLString("A");
        getHasFunctionalAbility.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class,
                () -> authenticationController.getHasFunctionalAbility(getHasFunctionalAbility));
    }

    @Test
    public void testGetUserInfoFail() {
        GetUserInfo getUserInfo = new GetUserInfo();
        getUserInfo.setXMLString("A");
        getUserInfo.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> informationController.getUserInfo(getUserInfo));
    }

    @Test
    public void testGetDeviceInfoFail() {
        GetDeviceInfo getDeviceInfo = new GetDeviceInfo();
        getDeviceInfo.setXMLString("A");
        getDeviceInfo.setUserTokenString("A");
        Assertions.assertThrows(
                ORDSException.class, () -> informationController.getDeviceInfo(getDeviceInfo));
    }
}
