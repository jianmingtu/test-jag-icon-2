package ca.bc.gov.open.icon.models;

import lombok.Data;

@Data
public class GetHealthResponse {
    protected String appid;
    protected String method;
    protected String status;
    protected String host;
    protected String instance;
    protected String version;
    protected String compatibility;
}
