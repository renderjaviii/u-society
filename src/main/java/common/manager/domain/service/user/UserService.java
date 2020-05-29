package common.manager.domain.service.user;

import common.manager.app.api.TokenApi;
import common.manager.app.api.UserApi;
import common.manager.app.rest.request.CreateUserRequest;
import common.manager.app.rest.request.UserLoginRequest;
import common.manager.domain.exception.GenericException;

public interface UserService {

    UserApi create(CreateUserRequest request) throws GenericException;

    UserApi get(String username) throws GenericException;

    void enableAccount(String username, String otpCode) throws GenericException;

    TokenApi login(UserLoginRequest request) throws GenericException;

}
