package usociety.manager.domain.util.mapper.impl;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.lang.Boolean.FALSE;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import usociety.manager.domain.util.mapper.CustomObjectMapper;

@Component
public class CustomObjectMapperImpl implements CustomObjectMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomObjectMapperImpl() {
        objectMapper.configure(FAIL_ON_NULL_FOR_PRIMITIVES, FALSE);
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, FALSE);
        objectMapper.configure(FAIL_ON_NUMBERS_FOR_ENUMS, FALSE);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(WRITE_DATES_AS_TIMESTAMPS);

    }

    @Override
    public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(content, valueType);
    }

    @Override
    public <T> T readValue(String content, TypeReference<T> valueTypeRef) throws JsonProcessingException {
        return objectMapper.readValue(content, valueTypeRef);
    }

    @Override
    public String writeValueAsString(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    @Override
    public <T> T convertValue(Object value, Class<T> valueType) {
        return objectMapper.convertValue(value, valueType);
    }

}
