package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.configuration.SoapConfig.ACCOUNT_TYPE_FIVE;
import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.bcs.*;
import ca.bc.gov.open.icon.biometrics.Search;
import ca.bc.gov.open.icon.biometrics.StartSearch;
import ca.bc.gov.open.icon.configuration.SoapConfig;
import ca.bc.gov.open.icon.exceptions.APIThrownException;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@Slf4j
public class SearchController {

    @Value("${icon.bsc-host}")
    private String bcsHost = "https://127.0.0.1/";

    @Value("${icon.online-service-id}")
    private String onlineServiceId;

    private final WebServiceTemplate soapTemplate;
    private final ObjectMapper objectMapper;

    public SearchController(WebServiceTemplate soapTemplate, ObjectMapper objectMapper) {
        this.soapTemplate = soapTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "startSearch")
    @ResponsePayload
    public ca.bc.gov.open.icon.biometrics.StartSearchResponse startSearch(
            @RequestPayload StartSearch startSearch) throws JsonProcessingException {
        try {
            ca.bc.gov.open.icon.bcs.StartSearch startSearchBCS =
                    new ca.bc.gov.open.icon.bcs.StartSearch();
            StartSearchRequest startSearchRequest = new StartSearchRequest();
            startSearchRequest.setRequesterUserId(startSearch.getRequestorUserId());
            startSearchRequest.setOnlineServiceId(onlineServiceId);
            startSearchRequest.setActiveOnly(
                    ActiveCodeRequest.fromValue(startSearch.getActiveOnly()));
            startSearchRequest.setRequesterAccountTypeCode(
                    BCeIDAccountTypeCode.fromValue(startSearch.getRequestorType()));
            startSearchRequest.setAccountType(ACCOUNT_TYPE_FIVE);

            startSearchBCS.setRequest(startSearchRequest);

            ca.bc.gov.open.icon.bcs.StartSearchResponse bcsResp = null;
            try {
                bcsResp =
                        (ca.bc.gov.open.icon.bcs.StartSearchResponse)
                                soapTemplate.marshalSendAndReceive(bcsHost, startSearchBCS);
            } catch (Exception ex) {
                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - BCS Service",
                                        "startSearch",
                                        ex.getMessage(),
                                        startSearchBCS)),
                        ex.getMessage());
            }

            if (!bcsResp.getStartSearchResult().getCode().equals(ResponseCode.SUCCESS)) {
                var exception =
                        "Failed to start BCS search " + bcsResp.getStartSearchResult().getMessage();

                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - BCS Service",
                                        "startSearch",
                                        exception,
                                        startSearchBCS)),
                        exception);
            }

            ca.bc.gov.open.icon.biometrics.StartSearchResponse out =
                    new ca.bc.gov.open.icon.biometrics.StartSearchResponse();
            Search search = new Search();
            search.setId(bcsResp.getStartSearchResult().getSearch().getSearchID());
            search.setUrl(bcsResp.getStartSearchResult().getSearch().getSearchURL());
            search.setExpiryDate(bcsResp.getStartSearchResult().getSearch().getExpiry().toString());
            out.setSearch(search);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "startSearch")));

            return out;
        } catch (APIThrownException ex) {
            log.error(ex.getLog());
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Processing failed",
                                    "startSearch",
                                    ex.getMessage(),
                                    startSearch)));
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "finishSearch")
    @ResponsePayload
    public ca.bc.gov.open.icon.biometrics.FinishSearchResponse finishSearch(
            @RequestPayload ca.bc.gov.open.icon.biometrics.FinishSearch finishSearch)
            throws JsonProcessingException {
        try {

            FinishSearch finishSearchBCS = new FinishSearch();
            FinishSearchRequest finishSearchRequest = new FinishSearchRequest();
            finishSearchRequest.setSearchID(finishSearch.getSearchId());
            finishSearchRequest.setRequesterUserId(finishSearch.getRequestorUserId());
            finishSearchRequest.setRequesterAccountTypeCode(
                    BCeIDAccountTypeCode.fromValue(finishSearch.getRequestorType()));
            finishSearchRequest.setOnlineServiceId(onlineServiceId);

            finishSearchBCS.setRequest(finishSearchRequest);

            FinishSearchResponse bcsResp = null;
            try {
                bcsResp =
                        (FinishSearchResponse)
                                soapTemplate.marshalSendAndReceive(bcsHost, finishSearchBCS);
            } catch (Exception ex) {
                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - BCS Service",
                                        "finishSearch",
                                        ex.getMessage(),
                                        finishSearch)),
                        ex.getMessage());
            }

            if (!bcsResp.getFinishSearchResult().getCode().equals(ResponseCode.SUCCESS)) {
                var exception =
                        "Failed to finish search " + bcsResp.getFinishSearchResult().getMessage();
                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - BCS Service",
                                        "finishSearch",
                                        exception,
                                        finishSearch)),
                        exception);
            }

            ca.bc.gov.open.icon.biometrics.FinishSearchResponse out =
                    new ca.bc.gov.open.icon.biometrics.FinishSearchResponse();

            out.setActiveFlag(bcsResp.getFinishSearchResult().getActive().value());
            out.setClientId(bcsResp.getFinishSearchResult().getDID());
            out.setCredentialRef(bcsResp.getFinishSearchResult().getCredentialReference());

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "finishSearch")));
            return out;
        } catch (APIThrownException ex) {
            log.error(ex.getLog());
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Processing failed",
                                    "finishSearch",
                                    ex.getMessage(),
                                    finishSearch)));
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        }
    }
}
