package usociety.manager.domain.util.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface CustomObjectMapper {

    <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException;

    String writeValueAsString(Object value) throws JsonProcessingException;

    <T> T convertValue(Object value, Class<T> valueType);

}
