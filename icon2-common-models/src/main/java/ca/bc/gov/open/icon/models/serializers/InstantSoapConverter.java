package ca.bc.gov.open.icon.models.serializers;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class InstantSoapConverter {

    private InstantSoapConverter() {}

    public static String print(Instant value) {
        String out =
                DateTimeFormatter.ofPattern("yyyy.MM.dd")
                        .withZone(ZoneId.of("GMT-7"))
                        .withLocale(Locale.US)
                        .format(value);
        return out;
    }

    public static Instant parse(String value) {
        try {
            Date d;
            // Date time parser
            var sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT-7"));
            d = sdf.parse(value);
            return d.toInstant();
        } catch (Exception ex) {
            log.warn("Bad date received from soap request - invalid date format: " + value);
            return null;
        }
    }
}
