package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.myinfo.*;
import ca.bc.gov.open.icon.utils.XMLUtilities;
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
public class InformationController {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public InformationController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo", localPart = "getOrders")
    @ResponsePayload
    public GetOrdersResponse getOrders(@RequestPayload GetOrders getOrders)
            throws JsonProcessingException {

        GetOrdersDocument getOrdersDocument = new GetOrdersDocument();
        getOrdersDocument.setOrders(
                XMLUtilities.deserializeXmlStr(getOrders.getXMLString(), new Orders()));
        getOrdersDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(getOrders.getUserTokenString(), new UserToken()));

        HttpEntity<GetOrdersDocument> payload =
                new HttpEntity<>(getOrdersDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/orders");

        try {
            HttpEntity<Orders> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Orders.class);

            GetOrdersResponse getOrdersResponse = new GetOrdersResponse();
            getOrdersDocument.setOrders(resp.getBody());
            getOrdersResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(getOrdersDocument.getOrders()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getOrders")));
            return getOrdersResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getOrders",
                                    ex.getMessage(),
                                    getOrders)));
            throw handleError(ex, new ca.bc.gov.open.icon.myinfo.Error());
        }
    }

    @PayloadRoot(namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo", localPart = "getPrograms")
    @ResponsePayload
    public GetProgramsResponse getPrograms(@RequestPayload GetPrograms getPrograms)
            throws JsonProcessingException {

        GetProgramsDocument getProgramsDocument = new GetProgramsDocument();
        getProgramsDocument.setPrograms(
                XMLUtilities.deserializeXmlStr(getPrograms.getXMLString(), new Programs()));
        getProgramsDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(getPrograms.getUserTokenString(), new UserToken()));

        HttpEntity<GetProgramsDocument> payload =
                new HttpEntity<>(getProgramsDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/programs");

        try {
            HttpEntity<Programs> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Programs.class);

            GetProgramsResponse getProgramsResponse = new GetProgramsResponse();
            getProgramsDocument.setPrograms(resp.getBody());
            getProgramsResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(getProgramsDocument.getPrograms()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getPrograms")));
            return getProgramsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getPrograms",
                                    ex.getMessage(),
                                    getPrograms)));
            throw handleError(ex, new ca.bc.gov.open.icon.myinfo.Error());
        }
    }

    @PayloadRoot(namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo", localPart = "getLocations")
    @ResponsePayload
    public GetLocationsResponse getLocations(@RequestPayload GetLocations getLocations)
            throws JsonProcessingException {

        GetLocationsDocument getLocationsDocument = new GetLocationsDocument();
        getLocationsDocument.setLocations(
                XMLUtilities.deserializeXmlStr(getLocations.getXMLString(), new Locations()));
        getLocationsDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(getLocations.getUserTokenString(), new UserToken()));

        HttpEntity<GetLocationsDocument> payload =
                new HttpEntity<>(getLocationsDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/locations");

        try {
            HttpEntity<Locations> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Locations.class);

            GetLocationsResponse getLocationsResponse = new GetLocationsResponse();
            getLocationsDocument.setLocations(resp.getBody());
            getLocationsResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(getLocationsDocument.getLocations()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getLocations")));
            return getLocationsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getLocations",
                                    ex.getMessage(),
                                    getLocations)));
            throw handleError(ex, new ca.bc.gov.open.icon.myinfo.Error());
        }
    }

    @PayloadRoot(namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo", localPart = "getConditions")
    @ResponsePayload
    public GetConditionsResponse getConditions(@RequestPayload GetConditions getConditions)
            throws JsonProcessingException {

        GetConditionsDocument getConditionsDocument = new GetConditionsDocument();
        getConditionsDocument.setConditions(
                XMLUtilities.deserializeXmlStr(getConditions.getXMLString(), new Conditions()));
        getConditionsDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getConditions.getUserTokenString(), new UserToken()));

        HttpEntity<GetConditionsDocument> payload =
                new HttpEntity<>(getConditionsDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/conditions");

        try {
            HttpEntity<Conditions> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Conditions.class);

            GetConditionsResponse getConditionsResponse = new GetConditionsResponse();
            getConditionsDocument.setConditions(resp.getBody());
            getConditionsResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(getConditionsDocument.getConditions()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getConditions")));
            return getConditionsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getConditions",
                                    ex.getMessage(),
                                    getConditions)));
            throw handleError(ex, new ca.bc.gov.open.icon.myinfo.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo",
            localPart = "getOrdersConditions")
    @ResponsePayload
    public GetOrdersConditionsResponse getOrdersConditions(
            @RequestPayload GetOrdersConditions getOrdersConditions)
            throws JsonProcessingException {

        GetOrdersConditionsDocument getOrdersConditionsDocument = new GetOrdersConditionsDocument();
        getOrdersConditionsDocument.setOrdersConditions(
                XMLUtilities.deserializeXmlStr(
                        getOrdersConditions.getXMLString(), new OrdersConditions()));
        getOrdersConditionsDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getOrdersConditions.getUserTokenString(), new UserToken()));

        HttpEntity<GetOrdersConditions> payload =
                new HttpEntity<>(getOrdersConditions, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "information/orders-conditions");

        try {
            HttpEntity<OrdersConditions> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            OrdersConditions.class);

            GetOrdersConditionsResponse getOrdersConditionsResponse =
                    new GetOrdersConditionsResponse();
            getOrdersConditionsDocument.setOrdersConditions(resp.getBody());
            getOrdersConditionsResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(
                            getOrdersConditionsDocument.getOrdersConditions()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getOrdersConditions")));
            return getOrdersConditionsResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getOrdersConditions",
                                    ex.getMessage(),
                                    getOrdersConditions)));
            throw handleError(ex, new ca.bc.gov.open.icon.myinfo.Error());
        }
    }

    @PayloadRoot(namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo", localPart = "getDates")
    @ResponsePayload
    public GetDatesResponse getDates(@RequestPayload GetDates getDates)
            throws JsonProcessingException {

        GetDatesDocument getDatesDocument = new GetDatesDocument();
        getDatesDocument.setDates(
                XMLUtilities.deserializeXmlStr(getDates.getXMLString(), new Dates()));
        getDatesDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(getDates.getUserTokenString(), new UserToken()));

        HttpEntity<GetDatesDocument> payload =
                new HttpEntity<>(getDatesDocument, new HttpHeaders());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "information/dates");

        try {
            HttpEntity<Dates> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Dates.class);

            GetDatesResponse getDatesResponse = new GetDatesResponse();
            getDatesDocument.setDates(resp.getBody());
            getDatesResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(getDatesDocument.getDates()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getDates")));
            return getDatesResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getDates",
                                    ex.getMessage(),
                                    getDates)));
            throw handleError(ex, new ca.bc.gov.open.icon.myinfo.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.MyInfo.ws.provider:MyInfo",
            localPart = "getClientHistory")
    @ResponsePayload
    public GetClientHistoryResponse getClientHistory(
            @RequestPayload GetClientHistory getClientHistory) throws JsonProcessingException {

        GetClientHistoryDocument getClientHistoryDocument = new GetClientHistoryDocument();
        getClientHistoryDocument.setClientHistory(
                XMLUtilities.deserializeXmlStr(
                        getClientHistory.getXMLString(), new ClientHistory()));
        getClientHistoryDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getClientHistory.getUserTokenString(), new UserToken()));

        HttpEntity<GetClientHistoryDocument> payload =
                new HttpEntity<>(getClientHistoryDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "audit/client-history");

        try {
            HttpEntity<ClientHistory> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, ClientHistory.class);

            GetClientHistoryResponse getClientHistoryResponse = new GetClientHistoryResponse();
            getClientHistoryDocument.setClientHistory(resp.getBody());
            getClientHistoryResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(getClientHistoryDocument.getClientHistory()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getClientHistory")));
            return getClientHistoryResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getClientHistory",
                                    ex.getMessage(),
                                    getClientHistory)));
            throw handleError(ex, new ca.bc.gov.open.icon.myinfo.Error());
        }
    }
}
