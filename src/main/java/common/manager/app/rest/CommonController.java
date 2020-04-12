package common.manager.app.rest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public abstract class CommonController {

    protected String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().toString();
    }

    protected void validateUser(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!((OAuth2Authentication) authentication).isClientOnly() ||
                !authentication.getPrincipal().toString().equals(username)) {
            throw new UnapprovedClientAuthenticationException("Invalid credentials.");
        }
    }

}
