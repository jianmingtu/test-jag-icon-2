package ca.bc.gov.open.icon;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.bcs.*;
import ca.bc.gov.open.icon.biometrics.Deactivate;
import ca.bc.gov.open.icon.biometrics.Reactivate;
import ca.bc.gov.open.icon.controllers.ActivationController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActivationControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private WebServiceTemplate soapTemplate;
    @Mock private RestTemplate restTemplate;
    @Mock private ActivationController activationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        activationController = Mockito.spy(new ActivationController(soapTemplate, objectMapper));
    }

    @Test
    public void testReactivate() throws Exception {
        var req = new Reactivate();
        req.setCredentialRef("A");
        req.setRequestorUserId("A");
        req.setRequestorType("LDB");

        // Set up to mock soap service response
        var soapResp = new ReactivateCredentialResponse();
        var reactivateCredentialResponse2 = new ReactivateCredentialResponse2();
        reactivateCredentialResponse2.setCode(ResponseCode.SUCCESS);
        soapResp.setReactivateCredentialResult(reactivateCredentialResponse2);
        when(soapTemplate.marshalSendAndReceive(
                        anyString(), Mockito.any(ReactivateCredential.class)))
                .thenReturn(soapResp);

        var resp = activationController.reactivate(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testDeactivate() throws JsonProcessingException {
        var req = new Deactivate();
        req.setCredentialRef("A");
        req.setRequestorUserId("A");
        req.setRequestorType("LDB");

        // Set up to mock soap service response
        var soapResp = new DeactivateCredentialResponse();
        var reactivateCredentialResponse2 = new DeactivateCredentialResponse2();
        reactivateCredentialResponse2.setCode(ResponseCode.SUCCESS);
        soapResp.setDeactivateCredentialResult(reactivateCredentialResponse2);
        when(soapTemplate.marshalSendAndReceive(
                        anyString(), Mockito.any(DeactivateCredential.class)))
                .thenReturn(soapResp);

        var resp = activationController.deactivate(req);
        Assertions.assertNotNull(resp);
    }
}
