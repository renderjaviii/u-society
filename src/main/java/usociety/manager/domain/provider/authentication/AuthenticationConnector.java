package usociety.manager.domain.provider.authentication;

import usociety.manager.app.api.TokenApi;
import usociety.manager.app.rest.request.LoginRequest;

public interface AuthenticationConnector {

    TokenApi login(LoginRequest body);

}
