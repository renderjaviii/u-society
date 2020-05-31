package common.manager.domain.provider.authentication;

import common.manager.app.api.TokenApi;
import common.manager.app.rest.request.UserLoginRequest;

public interface AuthenticationConnector {

    TokenApi login(UserLoginRequest body);

}
