package org.manage.config;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Provider
public class LocalDateProvider implements ParamConverterProvider {

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if(!rawType.equals(LocalDate.class)){
            return null;
        }
        return new ParamConverter<>() {
            @Override
            public T fromString(String value) {
                return (T) LocalDate.parse(value, dateFormatter);
            }

            @Override
            public String toString(T value) {
                return ((LocalDate) value).format(dateFormatter);
            }
        };
    }
}
