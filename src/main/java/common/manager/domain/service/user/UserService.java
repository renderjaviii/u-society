package common.manager.domain.service.user;

import common.manager.app.api.TokenApi;
import common.manager.app.rest.request.UserLoginRequest;
import common.manager.domain.exception.GenericException;

public interface UserService {

  /*  CreateUserResponse create(CreateUserRequest request) throws GenericException;

    Authentication getTokenInfo();

    UserApi get(String username) throws GenericException;

    void enableAccount(String username, String otpCode) throws GenericException;*/

    TokenApi login(UserLoginRequest request) throws GenericException;

}
