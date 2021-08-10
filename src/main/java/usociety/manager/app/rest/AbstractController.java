package usociety.manager.app.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import usociety.manager.domain.exception.UserValidationException;

public abstract class AbstractController {

    @Autowired
    private HttpServletRequest httpServletRequest;

    protected String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().toString();
    }

    protected String validateUser(String username) throws UserValidationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (((OAuth2Authentication) authentication).isClientOnly() ||
                !authentication.getPrincipal().toString().equals(username)) {
            throw new UserValidationException("Invalid credentials.");
        }
        return username;
    }

    protected String getHeader(String name) {
        return httpServletRequest.getHeader(name);
    }

}
