package ca.bc.gov.open.icon.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class APIThrownException extends RuntimeException {

    String logMessage;

    public APIThrownException() {
        super(
                "An error response was received from server please check that your request is of valid form");
    }

    public APIThrownException(String logMessage, String message) {
        super(message);
        this.logMessage = logMessage;
    }

    public String getLog() {
        return logMessage;
    }
}
