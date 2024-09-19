package ca.bc.gov.open.sender.controllers;

import ca.bc.gov.open.icon.exceptions.RestTemplateException;
import ca.bc.gov.open.icon.models.GetHealthResponse;
import ca.bc.gov.open.icon.models.GetPingResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/")
@Slf4j
public class HealthCheckController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${icon.host}")
    private String ordsHost = "https://127.0.0.1/";

    @Autowired
    public HealthCheckController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "health")
    public GetHealthResponse getHealth() throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ordsHost + "health");

        try {
            HttpEntity<GetHealthResponse> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            GetHealthResponse.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getHealth")));

            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getHealth",
                                    ex.getMessage(),
                                    null)));
            throw new RestTemplateException(ex.getMessage());
        }
    }

    @GetMapping(value = "ping")
    public GetPingResponse getPing() throws JsonProcessingException {
        log.info("Successful Ping to Application");
        return new GetPingResponse("Success");
    }
}
