package ca.bc.gov.open.icon;

import ca.bc.gov.open.icon.controllers.FileController;
import ca.bc.gov.open.icon.exceptions.ORDSException;
import ca.bc.gov.open.icon.myfiles.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrdsErrorTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate;
    @Mock private FileController fileController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fileController = Mockito.spy(new FileController(restTemplate, objectMapper));
    }

    @Test
    public void testGetStatusFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> fileController.getClientClaims(new GetClientClaims()));
    }

    @Test
    public void testGetCsNumsByDateFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> fileController.getCsNumsByDate(new GetCsNumsByDate()));
    }

    @Test
    public void testGetAgencyFileFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> fileController.getAgencyFile(new GetAgencyFile()));
    }

    @Test
    public void testSetMessageFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> fileController.setMessage(new SetMessage()));
    }

    @Test
    public void testSetDisclosureFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> fileController.setDisclosure(new SetDisclosure()));
    }

    @Test
    public void testGetClientInfoFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> fileController.getClientInfo(new GetClientInfo()));
    }
}
