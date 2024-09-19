package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.controllers.AuditController;
import ca.bc.gov.open.icon.packageinfo.GetPackageInfo;
import ca.bc.gov.open.icon.packageinfo.GetPackageInfoResponse;
import ca.bc.gov.open.icon.session.*;
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
public class AuditControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate;
    @Mock private AuditController auditController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        auditController = Mockito.spy(new AuditController(restTemplate, objectMapper));
    }

    @Test
    public void testeServiceAccessed() throws JsonProcessingException {
        var req = new EServiceAccessed();
        var eService = new EService();
        Base base = new Base();
        base.setCsNumber("A");
        base.setDeviceNO("A");
        base.setSessionID("A");
        eService.setBase(base);
        eService.setEServiceCD("A");

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

        AuditController auditController = new AuditController(restTemplate, objectMapper);
        var resp = auditController.eServiceAccessed(req);

        Assertions.assertNotNull(resp);
    }

    @Test
    public void testeHomeScreenAccessed() throws JsonProcessingException {
        var req = new HomeScreenAccessed();
        var homeScreen = new HomeScreen();
        var base = new Base();
        base.setCsNumber("A");
        base.setDeviceNO("A");
        base.setSessionID("A");
        homeScreen.setBase(base);
        req.setHomeScreen(homeScreen);

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

        var auditController = new AuditController(restTemplate, objectMapper);
        var resp = auditController.homeScreenAccessed(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testSessionTimeoutExecuted() throws JsonProcessingException {
        var req = new SessionTimeoutExecuted();
        var sessionTimeout = new SessionTimeout();
        var base = new Base();
        base.setCsNumber("A");
        base.setDeviceNO("A");
        base.setSessionID("A");
        sessionTimeout.setBase(base);
        sessionTimeout.setEServiceCD("A");
        sessionTimeout.setEServiceFuntionCD("A");
        req.setSessionTimeout(sessionTimeout);

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

        AuditController auditController = new AuditController(restTemplate, objectMapper);
        var resp = auditController.sessionTimeoutExecuted(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testEServiceFunctionAccessed() throws JsonProcessingException {
        var req = new EServiceFunctionAccessed();
        var eServiceFunction = new EServiceFunction();
        var base = new Base();
        base.setCsNumber("A");
        base.setDeviceNO("A");
        base.setSessionID("A");
        eServiceFunction.setBase(base);
        eServiceFunction.setEServiceCD("A");
        eServiceFunction.setEServiceFunctionCD("A");
        req.setEServiceFunction(eServiceFunction);

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

        AuditController auditController = new AuditController(restTemplate, objectMapper);
        var resp = auditController.eServiceFunctionAccessed(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetPackageInfo() throws JsonProcessingException {
        var req = new GetPackageInfo();

        var getPackageInfoResponse = new GetPackageInfoResponse();
        getPackageInfoResponse.setXMLString("A");

        ResponseEntity<GetPackageInfoResponse> responseEntity =
                new ResponseEntity<>(getPackageInfoResponse, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetPackageInfoResponse>>any()))
                .thenReturn(responseEntity);

        AuditController auditController = new AuditController(restTemplate, objectMapper);
        var resp = auditController.getPackageInfo(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testGetSessionParameters() throws JsonProcessingException {
        GetSessionParametersDocument getSessionParametersDocument =
                new GetSessionParametersDocument();
        SessionParameters sessionParameters = new SessionParameters();
        SessionParameter sessionParameter = new SessionParameter();
        sessionParameter.setParameterCd("A");
        sessionParameter.setValue("A");
        sessionParameters.getSessionParameter().add(sessionParameter);
        getSessionParametersDocument.setSessionParameters(sessionParameters);

        GetSessionParameters req = new GetSessionParameters();
        req.setXMLString("A");

        ResponseEntity<SessionParameters> responseEntity =
                new ResponseEntity<>(sessionParameters, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<SessionParameters>>any()))
                .thenReturn(responseEntity);

        var resp = auditController.getSessionParameters(req);
        Assertions.assertNotNull(resp);
    }
}
