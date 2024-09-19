package ca.bc.gov.open.icon.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceFaultException extends RuntimeException {

    private Object error;

    public ServiceFaultException(Object error) {
        super("Fault returned by invoked service");
        this.error = error;
    }

    public ServiceFaultException(Throwable e, Object error) {
        super("Fault returned by invoked service", e);
        this.error = error;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public String getServerError(String msg) {
        var STATUS_MESSAGE = "status_message\":\"";
        var start = msg.indexOf(STATUS_MESSAGE);
        if (start != -1) {
            var end = msg.indexOf("\" }", start + STATUS_MESSAGE.length());
            msg = msg.substring(start + STATUS_MESSAGE.length(), end);
        }

        return msg;
    }

    public String getRestError(String reason) {
        int start = reason.indexOf("\"cause\": \"", 0);
        if (start != -1) {
            int end = reason.indexOf(",<EOL>", start + 1);
            if (end != -1) {
                reason = reason.substring(start + 10, end - 1);
            }
        }

        return reason;
    }

    public static RuntimeException handleError(Exception ex) {
        return handleError(ex, null);
    }

    public static RuntimeException handleError(Exception ex, Object error) {
        if (ex instanceof org.springframework.web.client.HttpServerErrorException
                || ex instanceof APIThrownException) {
            var faultExceExcption = new ServiceFaultException(error);
            String msg = faultExceExcption.getServerError(ex.getMessage());
            if (error == null) {
                faultExceExcption.setError(new Error(msg));
            } else if (error instanceof ca.bc.gov.open.icon.ereporting.Error) {
                ((ca.bc.gov.open.icon.ereporting.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.myinfo.Error) {
                ((ca.bc.gov.open.icon.myinfo.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.audit.Error) {
                ((ca.bc.gov.open.icon.audit.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.auth.Error) {
                ((ca.bc.gov.open.icon.auth.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.biometrics.Error) {
                ((ca.bc.gov.open.icon.biometrics.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.hsr.Error) {
                ((ca.bc.gov.open.icon.hsr.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.message.Error) {
                ((ca.bc.gov.open.icon.message.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.myfiles.Error) {
                ((ca.bc.gov.open.icon.myfiles.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.tombstone.Error) {
                ((ca.bc.gov.open.icon.tombstone.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.trustaccount.Error) {
                ((ca.bc.gov.open.icon.trustaccount.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.visitschedule.Error) {
                ((ca.bc.gov.open.icon.visitschedule.Error) error).setReason(msg);
            } else {
                faultExceExcption.setError(new Error(msg));
            }
            return faultExceExcption;
        } else if (ex instanceof org.springframework.web.client.RestClientException) {
            var faultExceExcption = new ServiceFaultException(error);
            String msg = faultExceExcption.getRestError(ex.getMessage());
            if (error == null) {
                faultExceExcption.setError(new Error(msg));
            } else if (error instanceof ca.bc.gov.open.icon.ereporting.Error) {
                ((ca.bc.gov.open.icon.ereporting.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.myinfo.Error) {
                ((ca.bc.gov.open.icon.myinfo.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.audit.Error) {
                ((ca.bc.gov.open.icon.audit.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.auth.Error) {
                ((ca.bc.gov.open.icon.auth.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.biometrics.Error) {
                ((ca.bc.gov.open.icon.biometrics.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.hsr.Error) {
                ((ca.bc.gov.open.icon.hsr.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.message.Error) {
                ((ca.bc.gov.open.icon.message.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.myfiles.Error) {
                ((ca.bc.gov.open.icon.myfiles.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.tombstone.Error) {
                ((ca.bc.gov.open.icon.tombstone.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.trustaccount.Error) {
                ((ca.bc.gov.open.icon.trustaccount.Error) error).setReason(msg);
            } else if (error instanceof ca.bc.gov.open.icon.visitschedule.Error) {
                ((ca.bc.gov.open.icon.visitschedule.Error) error).setReason(msg);
            } else {
                faultExceExcption.setError(new Error(msg));
            }
            return faultExceExcption;
        } else {
            return new ORDSException(ex.getMessage());
        }
    }
}
