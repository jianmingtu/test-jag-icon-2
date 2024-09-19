package ca.bc.gov.open.icon;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.controllers.ErrorHandlingController;
import ca.bc.gov.open.icon.error.SetErrorMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
public class ErrorHandlingControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private WebServiceTemplate webServiceTemplate;
    @Mock private RestTemplate restTemplate;
    @Mock private ErrorHandlingController errorHandlingController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        errorHandlingController =
                Mockito.spy(new ErrorHandlingController(restTemplate, objectMapper));
    }

    @Test
    public void testSetErrorMessage() throws JsonProcessingException {
        var req = new SetErrorMessage();
        req.setErrorMsg("A");
        req.setCertNm("A");
        req.setMsgCd("A");
        req.setMsgCd("A");

        var out = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(out, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Map>>any()))
                .thenReturn(responseEntity);

        var resp = errorHandlingController.setErrorMessage(req);
        Assertions.assertNotNull(resp);
    }
}
