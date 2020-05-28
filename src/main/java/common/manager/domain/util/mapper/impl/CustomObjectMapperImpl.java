package common.manager.domain.util.mapper.impl;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.lang.Boolean.FALSE;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.manager.domain.util.mapper.CustomObjectMapper;

@Component
public class CustomObjectMapperImpl implements CustomObjectMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomObjectMapperImpl() {
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, FALSE);
        objectMapper.configure(FAIL_ON_NULL_FOR_PRIMITIVES, FALSE);
        objectMapper.configure(FAIL_ON_NUMBERS_FOR_ENUMS, FALSE);
        //objectMapper.disable(WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public <T> T readValue(String content, Class<T> valueType) throws IOException {
        return objectMapper.readValue(content, valueType);
    }

    @Override
    public String writeValueAsString(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

}
