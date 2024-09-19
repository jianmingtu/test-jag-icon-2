package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.audit.*;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
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

    @PayloadRoot(namespace = "ICON2.Source.Audit.ws:Record", localPart = "ReauthenticationFailed")
    @ResponsePayload
    public ReauthenticationFailedResponse reauthenticationFailed(
            @RequestPayload ReauthenticationFailed reauthenticationFailed)
            throws JsonProcessingException {

        var inner =
                reauthenticationFailed.getReauthentication() != null
                        ? reauthenticationFailed.getReauthentication()
                        : new Reauthentication();

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "auth/reauthentication-failed");
        HttpEntity<Reauthentication> payload = new HttpEntity<>(inner, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "reauthenticationFailed")));
            ReauthenticationFailedResponse out = new ReauthenticationFailedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "reauthenticationFailed",
                                    ex.getMessage(),
                                    inner)));
            throw handleError(ex, new ca.bc.gov.open.icon.audit.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.Audit.ws:Record",
            localPart = "ReauthenticationSucceeded")
    @ResponsePayload
    public ReauthenticationSucceededResponse reauthenticationSucceeded(
            @RequestPayload ReauthenticationSucceeded reauthenticationSucceeded)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "auth/reauthentication-succeeded");
        Reauthentication inner =
                reauthenticationSucceeded != null
                                && reauthenticationSucceeded.getReauthentication() != null
                        ? reauthenticationSucceeded.getReauthentication()
                        : new Reauthentication();
        HttpEntity<Reauthentication> payload = new HttpEntity<>(inner, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "reauthenticationSucceeded")));

            ReauthenticationSucceededResponse out = new ReauthenticationSucceededResponse();
            out.setStatus(resp.getBody());
            return out;

        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "reauthenticationSucceeded",
                                    ex.getMessage(),
                                    reauthenticationSucceeded)));
            throw handleError(ex, new ca.bc.gov.open.icon.audit.Error());
        }
    }

    @PayloadRoot(namespace = "ICON2.Source.Audit.ws:Record", localPart = "LogoutExcecuted")
    @ResponsePayload
    public LogoutExcecutedResponse logoutExecuted(@RequestPayload LogoutExcecuted logoutExecuted)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "auth/logout-executed");
        Logout inner =
                logoutExecuted != null && logoutExecuted.getLogout() != null
                        ? logoutExecuted.getLogout()
                        : new Logout();
        HttpEntity<Logout> payload = new HttpEntity<>(inner, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "logoutExecuted")));
            LogoutExcecutedResponse out = new LogoutExcecutedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "logoutExecuted",
                                    ex.getMessage(),
                                    inner)));
            throw handleError(ex, new ca.bc.gov.open.icon.audit.Error());
        }
    }

    @PayloadRoot(namespace = "ICON2.Source.Audit.ws:Record", localPart = "IdleTimeoutExecuted")
    @ResponsePayload
    public IdleTimeoutExecutedResponse idleTimeoutExecuted(
            @RequestPayload IdleTimeoutExecuted idleTimeoutExecuted)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "auth/idle-timeout-executed");
        IdleTimeout inner =
                idleTimeoutExecuted != null && idleTimeoutExecuted.getIdleTimeout() != null
                        ? idleTimeoutExecuted.getIdleTimeout()
                        : new IdleTimeout();
        HttpEntity<IdleTimeout> payload = new HttpEntity<>(inner, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "idleTimeoutExecuted")));
            IdleTimeoutExecutedResponse out = new IdleTimeoutExecutedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "idleTimeoutExecuted",
                                    ex.getMessage(),
                                    inner)));
            throw handleError(ex, new ca.bc.gov.open.icon.audit.Error());
        }
    }

    @PayloadRoot(
            namespace = "ICON2.Source.Audit.ws:Record",
            localPart = "PrimaryAuthenticationCompleted")
    @ResponsePayload
    public PrimaryAuthenticationCompletedResponse primaryAuthenticationCompleted(
            @RequestPayload PrimaryAuthenticationCompleted primaryAuthentication)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "auth/primary-auth-completed");
        PrimaryAuthentication inner =
                primaryAuthentication != null
                                && primaryAuthentication.getPrimaryAuthentication() != null
                        ? primaryAuthentication.getPrimaryAuthentication()
                        : new PrimaryAuthentication();
        HttpEntity<PrimaryAuthentication> payload = new HttpEntity<>(inner, new HttpHeaders());

        try {
            HttpEntity<Status> resp =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, payload, Status.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "primaryAuthentication")));
            PrimaryAuthenticationCompletedResponse out =
                    new PrimaryAuthenticationCompletedResponse();
            out.setStatus(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "primaryAuthentication",
                                    ex.getMessage(),
                                    inner)));
            throw handleError(ex, new ca.bc.gov.open.icon.audit.Error());
        }
    }
}
