package ca.bc.gov.open.icon;

import ca.bc.gov.open.icon.biometrics.*;
import ca.bc.gov.open.icon.controllers.*;
import ca.bc.gov.open.icon.exceptions.ORDSException;
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
public class OrdsErrorTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private WebServiceTemplate webServiceTemplate;
    @Mock private RestTemplate restTemplate;
    @Mock private ActivationController activationController;
    @Mock private DidController didController;
    @Mock private EnrollmentController enrollmentController;
    @Mock private RemovalController removalController;
    @Mock private SearchController searchController;
    @Mock private WebServiceTemplate soapTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        activationController =
                Mockito.spy(new ActivationController(webServiceTemplate, objectMapper));
        didController = Mockito.spy(new DidController(webServiceTemplate, objectMapper));
        enrollmentController =
                Mockito.spy(
                        new EnrollmentController(webServiceTemplate, objectMapper, restTemplate));
        removalController =
                Mockito.spy(new RemovalController(webServiceTemplate, objectMapper, restTemplate));
        searchController = Mockito.spy(new SearchController(soapTemplate, objectMapper));
    }

    @Test
    public void testReactivateFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> activationController.reactivate(new Reactivate()));
    }

    @Test
    public void testDeactivateFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> activationController.deactivate(new Deactivate()));
    }

    @Test
    public void testGetDidFail() {
        Assertions.assertThrows(ORDSException.class, () -> didController.getDid(new GetDID()));
    }

    @Test
    public void testStartEnrollmentFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> enrollmentController.startEnrollment(new StartEnrollment()));
    }

    @Test
    public void testFinishEnrollmentFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> enrollmentController.finishEnrollment(new FinishEnrollment()));
    }

    @Test
    public void testMoveFail() {
        Assertions.assertThrows(ORDSException.class, () -> removalController.move(new Move()));
    }

    @Test
    public void testRemoveFail() {
        Assertions.assertThrows(ORDSException.class, () -> removalController.remove(new Remove()));
    }

    @Test
    public void testRemoveIdentityFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> removalController.removeIdentity(new RemoveIdentity()));
    }

    @Test
    public void testRemoveTemplateFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> removalController.removeTemplate(new RemoveTemplate()));
    }

    @Test
    public void testStartSearchFail() {
        Assertions.assertThrows(
                ORDSException.class, () -> searchController.startSearch(new StartSearch()));
    }

    @Test
    public void testFinishSearchFail() {
        Assertions.assertThrows(
                ORDSException.class,
                () ->
                        searchController.finishSearch(
                                new ca.bc.gov.open.icon.biometrics.FinishSearch()));
    }
}
