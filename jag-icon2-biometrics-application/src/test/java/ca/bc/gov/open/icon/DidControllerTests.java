package ca.bc.gov.open.icon;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.biometrics.GetDID;
import ca.bc.gov.open.icon.controllers.DidController;
import ca.bc.gov.open.icon.ips.BCeIDAccountTypeCode;
import ca.bc.gov.open.icon.ips.GetDIDRequest;
import ca.bc.gov.open.icon.ips.GetDIDResponse2;
import ca.bc.gov.open.icon.ips.ResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ws.client.core.WebServiceTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DidControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private WebServiceTemplate soapTemplate;
    @Mock private DidController didController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        didController = Mockito.spy(new DidController(soapTemplate, objectMapper));
    }

    @Test
    public void testGetDid() throws JsonProcessingException {
        var req = new GetDID();
        req.setRequestorType("Individual");
        req.setRequestorUserId("A");
        req.setIdRef("A");

        ca.bc.gov.open.icon.ips.GetDID getDIDIPS = new ca.bc.gov.open.icon.ips.GetDID();
        GetDIDRequest getDIDRequest = new GetDIDRequest();
        getDIDRequest.setRequesterUserId("A");
        getDIDRequest.setIdRef("A");
        getDIDRequest.setRequesterAccountTypeCode(BCeIDAccountTypeCode.fromValue("Individual"));
        getDIDRequest.setOnlineServiceId("A");

        getDIDIPS.setRequest(getDIDRequest);

        var soapResp = new ca.bc.gov.open.icon.ips.GetDIDResponse();
        var getDIDResponse2 = new GetDIDResponse2();
        getDIDResponse2.setCode(ResponseCode.SUCCESS);
        soapResp.setGetDIDResult(getDIDResponse2);
        when(soapTemplate.marshalSendAndReceive(
                        anyString(), Mockito.any(ca.bc.gov.open.icon.ips.GetDID.class)))
                .thenReturn(soapResp);

        var resp = didController.getDid(req);
        Assertions.assertNotNull(resp);
    }
}
