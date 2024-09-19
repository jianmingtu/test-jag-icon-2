package ca.bc.gov.open.icon.controllers;

import static ca.bc.gov.open.icon.configuration.SoapConfig.ACCOUNT_TYPE_FIVE;
import static ca.bc.gov.open.icon.exceptions.ServiceFaultException.handleError;

import ca.bc.gov.open.icon.bcs.FinishEnrollmentRequest;
import ca.bc.gov.open.icon.bcs.StartEnrollmentRequest;
import ca.bc.gov.open.icon.biometrics.*;
import ca.bc.gov.open.icon.configuration.SoapConfig;
import ca.bc.gov.open.icon.exceptions.APIThrownException;
import ca.bc.gov.open.icon.iis.BCeIDAccountTypeCode;
import ca.bc.gov.open.icon.iis.RegisterIndividual;
import ca.bc.gov.open.icon.iis.RegisterIndividualRequest;
import ca.bc.gov.open.icon.iis.RegisterIndividualResponse;
import ca.bc.gov.open.icon.ips.*;
import ca.bc.gov.open.icon.models.OrdsErrorLog;
import ca.bc.gov.open.icon.models.RequestSuccessLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@Slf4j
public class EnrollmentController {

    @Value("${icon.iis-host}")
    private String iisHost = "https://127.0.0.1/";

    @Value("${icon.ips-host}")
    private String ipsHost = "https://127.0.0.1/";

    @Value("${icon.bsc-host}")
    private String bcsHost = "https://127.0.0.1/";

    @Value("${icon.host}")
    private String ordsHost = "https://127.0.0.1/";

    @Value("${icon.online-service-id}")
    private String onlineServiceId;

    private final WebServiceTemplate soapTemplate;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public EnrollmentController(
            WebServiceTemplate soapTemplate, ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.soapTemplate = soapTemplate;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "startEnrollment")
    @ResponsePayload
    public StartEnrollmentResponse startEnrollment(@RequestPayload StartEnrollment startEnrollment)
            throws JsonProcessingException {

        try {
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(ordsHost + "client/did")
                            .queryParam("csNum", startEnrollment.getCsNum());

            HttpEntity<Map<String, String>> andidResp =
                    restTemplate.exchange(
                            builder.build().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            new ParameterizedTypeReference<>() {});

            String did = Objects.requireNonNull(andidResp.getBody()).getOrDefault("andid", "");

            //      Set up IIS Request
            RegisterIndividual iisReq = new RegisterIndividual();
            RegisterIndividualRequest issReqInner = new RegisterIndividualRequest();
            issReqInner.setOnlineServiceId(onlineServiceId);
            issReqInner.setRequesterUserId(startEnrollment.getRequestorUserId());
            issReqInner.setRequesterAccountTypeCode(
                    BCeIDAccountTypeCode.fromValue(startEnrollment.getRequestorType()));
            issReqInner.setAccountType(ACCOUNT_TYPE_FIVE);
            iisReq.setRequest(issReqInner);

            // The response is wrapped in 2 objects. Here we are doing the correct cast to receive
            // the object and then doing the unwrapping at the same time
            RegisterIndividualResponse registerIndividualResponse = null;
            try {
                registerIndividualResponse =
                        (RegisterIndividualResponse)
                                soapTemplate.marshalSendAndReceive(iisHost, iisReq);
            } catch (Exception ex) {
                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - IIS Service",
                                        "startEnrollment",
                                        ex.getMessage(),
                                        iisReq)),
                        ex.getMessage());
            }

            if (!registerIndividualResponse
                    .getRegisterIndividualResult()
                    .getCode()
                    .equals(ca.bc.gov.open.icon.iis.ResponseCode.SUCCESS)) {
                String exception =
                        "Failed to register individual "
                                + registerIndividualResponse
                                        .getRegisterIndividualResult()
                                        .getMessage();

                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - IIS Service",
                                        "startEnrollment",
                                        exception,
                                        iisReq)),
                        exception);
            }

            String idRef = registerIndividualResponse.getRegisterIndividualResult().getIdRef();

            Link ipsLink = new Link();
            LinkRequest ipsLinkInner = new LinkRequest();
            ipsLinkInner.setDID(did);
            ipsLinkInner.setRequesterUserId(startEnrollment.getRequestorUserId());
            ipsLinkInner.setIdRef(idRef);
            ipsLinkInner.setRequesterAccountTypeCode(
                    ca.bc.gov.open.icon.ips.BCeIDAccountTypeCode.fromValue(
                            startEnrollment.getRequestorType()));
            ipsLinkInner.setOnlineServiceId(onlineServiceId);
            ipsLink.setRequest(ipsLinkInner);

            //      We do nothing with the response so ignored
            LinkResponse linkResponse = null;
            try {
                linkResponse = (LinkResponse) soapTemplate.marshalSendAndReceive(ipsHost, ipsLink);
            } catch (Exception ex) {
                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - IPS Service",
                                        "startEnrollment",
                                        ex.getMessage(),
                                        ipsLink)),
                        ex.getMessage());
            }

            if (!linkResponse.getLinkResult().getCode().equals(ResponseCode.SUCCESS)) {
                var exception =
                        "Failed to get IPS Link " + linkResponse.getLinkResult().getMessage();
                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - IPS Service",
                                        "startEnrollment",
                                        exception,
                                        ipsLink)),
                        exception);
            }

            GetIdRef getIdRef = new GetIdRef();
            GetIdRefRequest getIdRefInner = new GetIdRefRequest();
            getIdRefInner.setDID(did);
            getIdRefInner.setOnlineServiceId(onlineServiceId);
            getIdRefInner.setRequesterUserId(startEnrollment.getRequestorUserId());
            getIdRefInner.setRequesterAccountTypeCode(
                    ca.bc.gov.open.icon.ips.BCeIDAccountTypeCode.fromValue(
                            startEnrollment.getRequestorType()));

            getIdRef.setRequest(getIdRefInner);

            GetIdRefResponse idRefResponse = null;
            try {
                idRefResponse =
                        (GetIdRefResponse) soapTemplate.marshalSendAndReceive(ipsHost, getIdRef);
            } catch (Exception ex) {
                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - IPS Service",
                                        "startEnrollment",
                                        ex.getMessage(),
                                        getIdRef)),
                        ex.getMessage());
            }

            if (!idRefResponse.getGetIdRefResult().getCode().equals(ResponseCode.SUCCESS)) {
                var exception =
                        "Failed to get ID Ref response "
                                + idRefResponse.getGetIdRefResult().getMessage();
                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - IPS Service",
                                        "startEnrollment",
                                        exception,
                                        getIdRef)),
                        exception);
            }

            idRef = idRefResponse.getGetIdRefResult().getIdRef();

            ca.bc.gov.open.icon.bcs.StartEnrollment startEnrollmentBCSRequest =
                    new ca.bc.gov.open.icon.bcs.StartEnrollment();
            StartEnrollmentRequest bcsReqInner = new StartEnrollmentRequest();
            bcsReqInner.setIDRef(idRef);
            bcsReqInner.setOnlineServiceId(onlineServiceId);
            bcsReqInner.setRequesterUserId(startEnrollment.getRequestorUserId());
            bcsReqInner.setRequesterAccountTypeCode(
                    ca.bc.gov.open.icon.bcs.BCeIDAccountTypeCode.fromValue(
                            startEnrollment.getRequestorType()));

            startEnrollmentBCSRequest.setRequest(bcsReqInner);

            ca.bc.gov.open.icon.bcs.StartEnrollmentResponse bcsResp = null;
            try {
                bcsResp =
                        (ca.bc.gov.open.icon.bcs.StartEnrollmentResponse)
                                soapTemplate.marshalSendAndReceive(
                                        bcsHost, startEnrollmentBCSRequest);
            } catch (Exception ex) {
                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - BCS Service",
                                        "startEnrollment",
                                        ex.getMessage(),
                                        startEnrollmentBCSRequest)),
                        ex.getMessage());
            }

            if (!bcsResp.getStartEnrollmentResult()
                    .getCode()
                    .equals(ca.bc.gov.open.icon.bcs.ResponseCode.SUCCESS)) {
                var exception =
                        "Failed to enroll in BCS "
                                + bcsResp.getStartEnrollmentResult().getMessage();
                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - BCS Service",
                                        "startEnrollment",
                                        exception,
                                        startEnrollmentBCSRequest)),
                        exception);
            }

            StartEnrollmentResponse startEnrollmentResponse = new StartEnrollmentResponse();

            Issuance issuance = new Issuance();
            issuance.setId(bcsResp.getStartEnrollmentResult().getIssuance().getIssuanceID());
            issuance.setUrl(bcsResp.getStartEnrollmentResult().getIssuance().getEnrollmentURL());
            issuance.setExpiryDate(bcsResp.getStartEnrollmentResult().getIssuance().getExpiry());
            startEnrollmentResponse.setIssuance(issuance);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "startEnrollment")));

            return startEnrollmentResponse;

        } catch (APIThrownException ex) {
            log.error(ex.getLog());
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Processing failed",
                                    "startEnrollment",
                                    ex.getMessage(),
                                    startEnrollment)));
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "finishEnrollment")
    @ResponsePayload
    public FinishEnrollmentResponse finishEnrollment(
            @RequestPayload FinishEnrollment finishEnrollment) throws JsonProcessingException {
        try {
            ca.bc.gov.open.icon.bcs.FinishEnrollment finishEnrollmentBCSReq =
                    new ca.bc.gov.open.icon.bcs.FinishEnrollment();
            FinishEnrollmentRequest finishEnrollmentRequest = new FinishEnrollmentRequest();
            finishEnrollmentRequest.setOnlineServiceId(onlineServiceId);
            finishEnrollmentRequest.setRequesterUserId(finishEnrollment.getRequestorUserId());
            finishEnrollmentRequest.setIssuanceID(finishEnrollment.getIssuanceId());
            finishEnrollmentRequest.setRequesterAccountTypeCode(
                    ca.bc.gov.open.icon.bcs.BCeIDAccountTypeCode.fromValue(
                            finishEnrollment.getRequestorType()));
            finishEnrollmentBCSReq.setRequest(finishEnrollmentRequest);

            ca.bc.gov.open.icon.bcs.FinishEnrollmentResponse bcsResp = null;
            try {
                bcsResp =
                        (ca.bc.gov.open.icon.bcs.FinishEnrollmentResponse)
                                soapTemplate.marshalSendAndReceive(bcsHost, finishEnrollmentBCSReq);
            } catch (Exception ex) {
                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - BCS Service",
                                        "finishEnrollment",
                                        ex.getMessage(),
                                        finishEnrollmentBCSReq)),
                        ex.getMessage());
            }

            if (!bcsResp.getFinishEnrollmentResult()
                    .getCode()
                    .equals(ca.bc.gov.open.icon.bcs.ResponseCode.SUCCESS)) {
                var exception =
                        "Failed to finish BCS enrollment "
                                + bcsResp.getFinishEnrollmentResult().getMessage();
                throw new APIThrownException(
                        objectMapper.writeValueAsString(
                                new OrdsErrorLog(
                                        "Error received from WebService - BCS Service",
                                        "finishEnrollment",
                                        exception,
                                        finishEnrollmentBCSReq)),
                        exception);
            }

            FinishEnrollmentResponse out = new FinishEnrollmentResponse();
            out.setCredentialRef(bcsResp.getFinishEnrollmentResult().getCredentialReference());

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "finishEnrollment")));
            return out;
        } catch (APIThrownException ex) {
            log.error(ex.getLog());
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Processing failed",
                                    "finishEnrollment",
                                    ex.getMessage(),
                                    finishEnrollment)));
            throw handleError(ex, new ca.bc.gov.open.icon.biometrics.Error());
        }
    }
}
