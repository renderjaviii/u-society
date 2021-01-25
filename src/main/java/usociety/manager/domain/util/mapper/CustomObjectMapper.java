package usociety.manager.domain.util.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface CustomObjectMapper {

    <T> T readValue(String content, Class<T> valueType) throws IOException;

    String writeValueAsString(Object value) throws JsonProcessingException;

}
