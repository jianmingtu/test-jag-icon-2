package ca.bc.gov.open.icon.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HsrStatusLog {
    private String message;
    private String method;
    private String exception;
    private Object request;

    public HsrStatusLog(String message, String method) {
        this.message = message;
        this.method = method;
    }
}
