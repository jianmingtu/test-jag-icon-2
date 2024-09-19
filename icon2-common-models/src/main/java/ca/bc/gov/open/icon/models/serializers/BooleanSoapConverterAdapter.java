package ca.bc.gov.open.icon.models.serializers;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class BooleanSoapConverterAdapter extends XmlAdapter<String, Boolean> {

    public Boolean unmarshal(String value) {
        return BooleanSoapConverter.parse(value);
    }

    public String marshal(Boolean value) {
        return BooleanSoapConverter.print(value);
    }
}
