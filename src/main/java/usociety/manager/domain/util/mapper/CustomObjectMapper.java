package usociety.manager.domain.util.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

public interface CustomObjectMapper {

    <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException;

    <T> T readValue(String content, TypeReference<T> valueTypeRef) throws JsonProcessingException;

    String writeValueAsString(Object value) throws JsonProcessingException;

    <T> T convertValue(Object value, Class<T> valueType);

}
