package usociety.manager;

import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;

import usociety.manager.domain.util.mapper.CustomObjectMapper;
import usociety.manager.domain.util.mapper.impl.CustomObjectMapperImpl;

public abstract class TestUtils {

    protected static final CustomObjectMapper mapper = new CustomObjectMapperImpl();
    protected static final String USERNAME = "username";

    protected final OAuth2Authentication auth2Authentication;

    public TestUtils() {
        auth2Authentication = new OAuth2Authentication(
                new OAuth2Request(
                        Collections.emptyMap(),
                        USERNAME,
                        Collections.singletonList(new SimpleGrantedAuthority("ADMIN")),
                        TRUE,
                        Collections.singleton("WEB"),
                        Collections.emptySet(),
                        EMPTY,
                        Collections.emptySet(),
                        Collections.emptyMap()),
                new UsernamePasswordAuthenticationToken(USERNAME, EMPTY));
    }

    protected String toJson(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException ignored) {
        }
        return EMPTY;
    }

    protected <T> T readMvcResultValue(MvcResult mvcResult, Class<T> clazz) {
        try {
            String responseContent = mvcResult.getResponse().getContentAsString();
            if (StringUtils.isEmpty(responseContent)) {
                return null;
            }
            return mapper.readValue(responseContent, clazz);
        } catch (JsonProcessingException | UnsupportedEncodingException ex) {
            throw new RuntimeException("Error reading value", ex);
        }
    }

}
