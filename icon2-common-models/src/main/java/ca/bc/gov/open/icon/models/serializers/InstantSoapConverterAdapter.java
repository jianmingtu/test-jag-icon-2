package ca.bc.gov.open.icon.models.serializers;

import java.time.Instant;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class InstantSoapConverterAdapter extends XmlAdapter<String, Instant> {
    public Instant unmarshal(String value) {
        return InstantSoapConverter.parse(value);
    }

    public String marshal(Instant value) {
        return InstantSoapConverter.print(value);
    }
}
