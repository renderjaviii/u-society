package common.manager.domain.provider.authentication;

import common.manager.app.rest.request.UserLoginRequest;
import common.manager.domain.provider.authentication.dto.TokenDTO;

public interface AuthenticationConnector {

    TokenDTO login(UserLoginRequest request);

}
