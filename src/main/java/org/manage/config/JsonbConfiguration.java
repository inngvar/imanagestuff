package org.manage.config;

import io.quarkus.jsonb.JsonbConfigCustomizer;

import javax.inject.Singleton;
import javax.json.bind.JsonbConfig;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Jsonb Configuration
 * Further details https://quarkus.io/guides/rest-json#configuring-json-support
 */
@Singleton
public class JsonbConfiguration implements JsonbConfigCustomizer {

    @Override
    public void customize(JsonbConfig config) {
        config
            .withSerializers(new CustomSerializer())
            .withDateFormat(Constants.DATE_TIME_FORMAT, new Locale("ru-RU"));
    }

    public class CustomSerializer implements JsonbSerializer<LocalDate> {

        @Override
        public void serialize(LocalDate obj, JsonGenerator generator, SerializationContext ctx) {
                generator.write(LocalDateProvider.dateFormatter.format(obj));

        }
    }

}
