package usociety.manager.domain.provider.authentication;

import usociety.manager.app.api.TokenApi;
import usociety.manager.app.rest.request.UserLoginRequest;

public interface AuthenticationConnector {

    TokenApi login(UserLoginRequest body);

}
