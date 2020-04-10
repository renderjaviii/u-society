package common.manager.app.rest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import common.manager.domain.exception.GenericException;

public abstract class CommonController {

    protected String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().toString();
    }

    protected void validateUser(String username) throws GenericException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getPrincipal().toString().equals(username)) {
            throw new GenericException("Invalid credentials.", "FORBIDDEN");
        }
    }

}
