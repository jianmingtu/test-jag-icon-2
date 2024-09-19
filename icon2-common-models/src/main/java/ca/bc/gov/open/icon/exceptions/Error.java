package ca.bc.gov.open.icon.exceptions;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Error",
        propOrder = {"reason"})
@XmlRootElement(name = "Error")
public class Error {
    @XmlElement(name = "Reason")
    private String reason = "";

    public Error() {
        this.reason = "";
    }

    public Error(String reason) {
        this.reason = reason;
    }
}
