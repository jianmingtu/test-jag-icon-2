package ca.bc.gov.open.icon.models.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InstantDeserializer extends JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        try {
            var sfd = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
            sfd.setTimeZone(TimeZone.getTimeZone("GMT-7"));
            return sfd.parse(jsonParser.getText()).toInstant();
        } catch (ParseException e) {
            log.error(e.getLocalizedMessage());
        }
        return null;
    }
}
