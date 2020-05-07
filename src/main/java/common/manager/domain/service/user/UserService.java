package common.manager.domain.service.user;

import org.springframework.security.core.Authentication;

import common.manager.app.api.UserApi;
import common.manager.app.rest.request.CreateUserRequest;
import common.manager.app.rest.response.CreateUserResponse;
import common.manager.domain.exception.GenericException;

public interface UserService {

    CreateUserResponse create(CreateUserRequest request) throws GenericException;

    Authentication getTokenInfo();

    UserApi get(String username) throws GenericException;

    void enableAccount(String username, String otpCode) throws GenericException;

}
