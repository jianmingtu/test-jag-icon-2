package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.tombstone.*;
import ca.bc.gov.open.icon.trustaccount.*;
import ca.bc.gov.open.icon.trustaccount.UserToken;
import ca.bc.gov.open.icon.utils.XMLUtilities;
import ca.bc.gov.open.icon.visitschedule.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@Slf4j
public class ClientController {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ClientController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(
            namespace = "ICON2.Source.TombStoneInfo.ws.provider:TombStoneInfo",
            localPart = "getTombStoneInfo")
    @ResponsePayload
    public GetTombStoneInfoResponse getTombStoneInfo(
            @RequestPayload GetTombStoneInfo getTombStoneInfo) throws JsonProcessingException {

        GetTombStoneInfoDocument getTombStoneInfoDocument = new GetTombStoneInfoDocument();
        getTombStoneInfoDocument.setTombStoneInfo(
                XMLUtilities.deserializeXmlStr(
                        getTombStoneInfo.getXMLString(), new TombStoneInfo()));

        HttpEntity<GetTombStoneInfoDocument> payload =
                new HttpEntity<>(getTombStoneInfoDocument, new HttpHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "tombstone-info");

        try {
            HttpEntity<TombStoneInfo> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, TombStoneInfo.class);

            GetTombStoneInfoResponse getTombStoneInfoResponse = new GetTombStoneInfoResponse();
            getTombStoneInfoDocument.setTombStoneInfo(resp.getBody());
            if (getTombStoneInfoDocument.getTombStoneInfo() != null
                    && getTombStoneInfoDocument.getTombStoneInfo().getLatestPhoto() != null) {
                getTombStoneInfoDocument
                        .getTombStoneInfo()
                        .setLatestPhoto(
                                getTombStoneInfoDocument
                                        .getTombStoneInfo()
                                        .getLatestPhoto()
                                        .replaceAll("\r\n", "\n"));
            }
            getTombStoneInfoResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(getTombStoneInfoDocument.getTombStoneInfo()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getTombStoneInfo")));

            return getTombStoneInfoResponse;

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getTombStoneInfo",
                                    ex.getMessage(),
                                    getTombStoneInfo)));
            throw handleError(ex, new ca.bc.gov.open.icon.tombstone.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.TrustAccount.ws.provider:TrustAccount",
            localPart = "getTrustAccount")
    @ResponsePayload
    public GetTrustAccountResponse getTrustAccount(@RequestPayload GetTrustAccount getTrustAccount)
            throws JsonProcessingException {

        GetTrustAccountDocument doc = new GetTrustAccountDocument();
        doc.setTrustAccount(
                XMLUtilities.deserializeXmlStr(getTrustAccount.getXMLString(), new TrustAccount()));
        doc.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getTrustAccount.getUserTokenString(), new UserToken()));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "trust-account");
        HttpEntity<GetTrustAccountDocument> payload = new HttpEntity<>(doc, new HttpHeaders());
        try {
            HttpEntity<TrustAccount> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, TrustAccount.class);

            doc.setTrustAccount(resp.getBody());
            GetTrustAccountResponse getTrustAccountResponse = new GetTrustAccountResponse();
            getTrustAccountResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(doc.getTrustAccount()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getTrustAccount")));

            return getTrustAccountResponse;

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getTrustAccount",
                                    ex.getMessage(),
                                    getTrustAccount)));
            throw handleError(ex, new ca.bc.gov.open.icon.trustaccount.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.VisitSchedule.ws.provider:VisitSchedule",
            localPart = "getVisitSchedule")
    @ResponsePayload
    public GetVisitScheduleResponse getVisitSchedule(
            @RequestPayload GetVisitSchedule getVisitSchedule) throws JsonProcessingException {

        GetVisitScheduleDocument doc = new GetVisitScheduleDocument();
        doc.setVisitSchedule(
                XMLUtilities.deserializeXmlStr(
                        getVisitSchedule.getXMLString(), new VisitSchedule()));
        doc.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getVisitSchedule.getUserTokenString(),
                        new ca.bc.gov.open.icon.visitschedule.UserToken()));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "visit-schedule");
        HttpEntity<GetVisitScheduleDocument> payload = new HttpEntity<>(doc, new HttpHeaders());
        try {
            HttpEntity<VisitSchedule> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, VisitSchedule.class);

            doc.setVisitSchedule(resp.getBody());
            GetVisitScheduleResponse getVisitScheduleResponse = new GetVisitScheduleResponse();
            getVisitScheduleResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(doc.getVisitSchedule()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getVisitSchedule")));

            return getVisitScheduleResponse;

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getVisitSchedule",
                                    ex.getMessage(),
                                    getVisitSchedule)));
            throw handleError(ex, new ca.bc.gov.open.icon.visitschedule.Error());
        }
    }
}
