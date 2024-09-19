package ca.bc.gov.open.icon;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ca.bc.gov.open.icon.bcs.*;
import ca.bc.gov.open.icon.biometrics.StartSearch;
import ca.bc.gov.open.icon.controllers.SearchController;
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
public class SearchControllerTests {

    @Mock private ObjectMapper objectMapper;
    @Mock private WebServiceTemplate soapTemplate;
    @Mock private SearchController searchController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        searchController = Mockito.spy(new SearchController(soapTemplate, objectMapper));
    }

    @Test
    public void testStartSearch() throws JsonProcessingException {
        var req = new StartSearch();
        req.setRequestorType("Individual");
        req.setRequestorUserId("A");
        req.setActiveOnly("Y");

        // Set up to mock soap service response
        var soapResp = new StartSearchResponse();
        var startSearchResponse2 = new StartSearchResponse2();
        soapResp.setStartSearchResult(startSearchResponse2);
        var searchToken = new SearchToken();
        searchToken.setSearchID("A");
        searchToken.setSearchURL("A");
        searchToken.setExpiry("A");
        startSearchResponse2.setSearch(searchToken);
        startSearchResponse2.setCode(ResponseCode.SUCCESS);

        when(soapTemplate.marshalSendAndReceive(
                        anyString(), Mockito.any(ca.bc.gov.open.icon.bcs.StartSearch.class)))
                .thenReturn(soapResp);

        var resp = searchController.startSearch(req);
        Assertions.assertNotNull(resp);
    }

    @Test
    public void testFinishSearch() throws JsonProcessingException {
        var req = new ca.bc.gov.open.icon.biometrics.FinishSearch();
        req.setRequestorType("Individual");
        req.setRequestorUserId("A");
        req.setSearchId("A");

        // Set up to mock soap service response
        var soapResp = new FinishSearchResponse();
        var finishSearchResponse2 = new FinishSearchResponse2();
        finishSearchResponse2.setCode(ResponseCode.SUCCESS);
        finishSearchResponse2.setActive(ActiveCodeResponse.valueOf("Y"));
        finishSearchResponse2.setStatus(SearchStatusCode.FOUND);
        finishSearchResponse2.setDID("A");
        soapResp.setFinishSearchResult(finishSearchResponse2);

        when(soapTemplate.marshalSendAndReceive(anyString(), Mockito.any(FinishSearch.class)))
                .thenReturn(soapResp);

        var resp = searchController.finishSearch(req);
        Assertions.assertNotNull(resp);
    }
}
