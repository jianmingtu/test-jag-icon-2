package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.auth.*;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import ca.bc.gov.open.icon.utils.*;
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
public class AuthenticationController {
    @Value("${icon.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthenticationController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(
            namespace = "ICON2.Source.Authorization.ws.provider:AuthAuth",
            localPart = "getPreAuthorizeClient")
    @ResponsePayload
    public GetPreAuthorizeClientResponse getPreAuthorizeClient(
            @RequestPayload GetPreAuthorizeClient getPreAuthorizeClient)
            throws JsonProcessingException {

        GetPreAuthorizeClientDocument getPreAuthorizeClientDocument =
                new GetPreAuthorizeClientDocument();
        getPreAuthorizeClientDocument.setPreAuthorizeClient(
                XMLUtilities.deserializeXmlStr(
                        getPreAuthorizeClient.getXMLString(), new PreAuthorizeClient()));
        HttpEntity<GetPreAuthorizeClientDocument> payload =
                new HttpEntity<>(getPreAuthorizeClientDocument, new HttpHeaders());
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "auth/pre-auth-client");

        try {
            HttpEntity<PreAuthorizeClient> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            PreAuthorizeClient.class);

            GetPreAuthorizeClientResponse getPreAuthorizeClientResponse =
                    new GetPreAuthorizeClientResponse();
            getPreAuthorizeClientDocument.setPreAuthorizeClient(resp.getBody());
            getPreAuthorizeClientResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(
                            getPreAuthorizeClientDocument.getPreAuthorizeClient()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getPreAuthorizeClient")));
            return getPreAuthorizeClientResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getHasFunctionalAbility",
                                    ex.getMessage(),
                                    getPreAuthorizeClient)));
            throw handleError(ex, new ca.bc.gov.open.icon.auth.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.Authorization.ws.provider:AuthAuth",
            localPart = "getHasFunctionalAbility")
    @ResponsePayload
    public GetHasFunctionalAbilityResponse getHasFunctionalAbility(
            @RequestPayload GetHasFunctionalAbility getHasFunctionalAbility)
            throws JsonProcessingException {

        GetHasFunctionalAbilityDocument getHasFunctionalAbilityDocument =
                new GetHasFunctionalAbilityDocument();
        getHasFunctionalAbilityDocument.setHasFunctionalAbility(
                XMLUtilities.deserializeXmlStr(
                        getHasFunctionalAbility.getXMLString(), new HasFunctionalAbility()));
        getHasFunctionalAbilityDocument.setUserToken(
                XMLUtilities.deserializeXmlStr(
                        getHasFunctionalAbility.getUserTokenString(), new UserToken()));

        HttpEntity<GetHasFunctionalAbilityDocument> payload =
                new HttpEntity<>(getHasFunctionalAbilityDocument, new HttpHeaders());

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "auth/has-functional-ability");

        try {
            HttpEntity<HasFunctionalAbility> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            payload,
                            HasFunctionalAbility.class);

            GetHasFunctionalAbilityResponse getHasFunctionalAbilityResponse =
                    new GetHasFunctionalAbilityResponse();
            getHasFunctionalAbilityDocument.setHasFunctionalAbility(resp.getBody());
            getHasFunctionalAbilityResponse.setXMLString(
                    XMLUtilities.serializeXmlStr(
                            getHasFunctionalAbilityDocument.getHasFunctionalAbility()));

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getHasFunctionalAbility")));
            return getHasFunctionalAbilityResponse;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getHasFunctionalAbility",
                                    ex.getMessage(),
                                    getHasFunctionalAbility)));
            throw handleError(ex, new ca.bc.gov.open.icon.auth.Error());
        }
    }
}
