package usociety.manager.app.rest;

import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import com.fasterxml.jackson.core.JsonProcessingException;

import usociety.manager.domain.util.mapper.CustomObjectMapper;
import usociety.manager.domain.util.mapper.impl.CustomObjectMapperImpl;

public abstract class TestUtils {

    protected static final CustomObjectMapper mapper = new CustomObjectMapperImpl();
    protected static final String USERNAME = "username";

    protected final OAuth2Authentication auth2Authentication;

    public TestUtils() {
        auth2Authentication = new OAuth2Authentication(new OAuth2Request(
                Collections.emptyMap(),
                USERNAME,
                Collections.singletonList(new SimpleGrantedAuthority("ADMIN")),
                TRUE,
                Collections.singleton("WEB"),
                Collections.emptySet(),
                EMPTY,
                Collections.emptySet(),
                Collections.emptyMap()), null);
    }

    protected String toJson(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException ignored) {
        }
        return EMPTY;
    }

}
