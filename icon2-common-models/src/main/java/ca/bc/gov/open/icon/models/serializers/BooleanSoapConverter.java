package ca.bc.gov.open.icon.models.serializers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class BooleanSoapConverter {
    private BooleanSoapConverter() {}

    public static String print(boolean b) {
        return b ? "1" : "0";
    }

    public static Boolean parse(String s) {
        if (s == "1" || s == "0") return Boolean.valueOf(s.equals("1"));
        else {
            log.warn("Bad data received from soap request - invalid boolean: " + s);
            return null;
        }
    }
}
