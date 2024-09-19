package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.controllers.RecordController;
import ca.bc.gov.open.icon.ereporting.*;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecordControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private WebServiceTemplate webServiceTemplate;
    @Mock private RestTemplate restTemplate;
    @Mock private RecordController recordController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        recordController = Mockito.spy(new RecordController(restTemplate, objectMapper));
    }

    @Test
    public void testRecordCompleted() throws JsonProcessingException {
        var req = new RecordCompleted();
        var clientLogNotification = new ClientLogNotification();
        clientLogNotification.setCsNum("A");
        clientLogNotification.setEventID("A");
        clientLogNotification.setEventState("A");
        clientLogNotification.setDeviceNo("A");
        clientLogNotification.setMessage("A");
        clientLogNotification.setMessageType("A");
        clientLogNotification.setReauthTransactionNo("A");

        req.setXMLString("A");

        var out = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Map>>any()))
                .thenReturn(responseEntity);

        var resp = recordController.recordCompleted(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testRecordException() throws JsonProcessingException {
        var req = new RecordException();
        var clientLogNotification = new ClientLogNotification();
        clientLogNotification.setCsNum("A");
        clientLogNotification.setEventID("A");
        clientLogNotification.setEventState("A");
        clientLogNotification.setDeviceNo("A");
        clientLogNotification.setMessage("A");
        clientLogNotification.setMessageType("A");
        clientLogNotification.setReauthTransactionNo("A");

        req.setXMLString("A");

        var out = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Map>>any()))
                .thenReturn(responseEntity);

        var resp = recordController.recordException(req);
        Assertions.assertNotNull(resp);
    }
}
