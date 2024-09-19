package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.auth.*;
import ca.bc.gov.open.icon.auth.UserToken;
import ca.bc.gov.open.icon.controllers.InformationController;
import ca.bc.gov.open.icon.myinfo.*;
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
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InformationControllerTests {

    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate;
    @Mock private InformationController controller;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = Mockito.spy(new InformationController(restTemplate, objectMapper));
    }

    @Test
    public void testGetUserInfo() throws JsonProcessingException {

        UserInfo userInfo = new UserInfo();
        userInfo.setCsNum("A");
        userInfo.setBusinessRole("A");
        userInfo.setFirstName("A");
        userInfo.setLastName("A");
        userInfo.setLatestPhoto("A");
        userInfo.setLocationCD("A");

        FunctionalAbility functionalAbility = new FunctionalAbility();
        functionalAbility.setFunctionCd("A");
        functionalAbility.setServiceCd("A");

        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setSessionLimit("A");
        sessionInfo.setIdleTimeout("A");

        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setSessionInfo(sessionInfo);
        serviceInfo.setDescription("A");
        serviceInfo.getFunctionalAbility().add(functionalAbility);
        serviceInfo.setName("A");
        serviceInfo.setUrn("A");

        userInfo.getServiceInfo().add(serviceInfo);
        userInfo.setSessionInfo(sessionInfo);

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

        GetUserInfoDocument getUserInfoDocument = new GetUserInfoDocument();
        getUserInfoDocument.setUserInfo(userInfo);
        getUserInfoDocument.setUserToken(userToken);

        GetUserInfo req = new GetUserInfo();
        req.setXMLString(XMLUtilities.serializeXmlStr(getUserInfoDocument.getUserInfo()));
        req.setUserTokenString(XMLUtilities.serializeXmlStr(getUserInfoDocument.getUserToken()));

        // Set up to mock ords response
        ResponseEntity<UserInfo> responseEntity = new ResponseEntity<>(userInfo, HttpStatus.OK);
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<UserInfo>>any()))
                .thenReturn(responseEntity);

        GetUserInfoResponse resp = controller.getUserInfo(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetDeviceInfo() throws JsonProcessingException {

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceNo("A");
        deviceInfo.setBusinessRole("A");
        deviceInfo.setCertificateName("A");
        deviceInfo.setIsEnabled("A");
        deviceInfo.setLocationCd("A");
        deviceInfo.setPollActiveInterval("A");
        deviceInfo.setPollSleepInterval("A");
        deviceInfo.setSystemMessage("A");

        ServiceCodes serviceCodes = new ServiceCodes();
        serviceCodes.setServiceCd("A");
        deviceInfo.getServiceCodes().add(serviceCodes);

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

        GetDeviceInfoDocument getDeviceInfoDocument = new GetDeviceInfoDocument();
        getDeviceInfoDocument.setDeviceInfo(deviceInfo);
        getDeviceInfoDocument.setUserToken(userToken);

        GetDeviceInfo req = new GetDeviceInfo();
        req.setXMLString(XMLUtilities.serializeXmlStr(getDeviceInfoDocument.getDeviceInfo()));
        req.setUserTokenString(XMLUtilities.serializeXmlStr(getDeviceInfoDocument.getUserToken()));

        // Set up to mock ords response
        ResponseEntity<DeviceInfo> responseEntity = new ResponseEntity<>(deviceInfo, HttpStatus.OK);
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<DeviceInfo>>any()))
                .thenReturn(responseEntity);

        GetDeviceInfoResponse resp = controller.getDeviceInfo(req);
        Assertions.assertNotNull(resp);
    }
}
