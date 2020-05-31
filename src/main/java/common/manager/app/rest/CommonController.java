package common.manager.app.rest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import common.manager.domain.exception.UserValidationException;

public abstract class CommonController {

    protected String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().toString();
    }

    protected String validateUser(String username) throws UserValidationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (((OAuth2Authentication) authentication).isClientOnly() ||
                !authentication.getPrincipal().toString().equals(username)) {
            throw new UserValidationException("Invalid credentials.", "INVALID_CREDENTIALS");
        }
        return username;
    }

}
