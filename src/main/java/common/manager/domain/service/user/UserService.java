package common.manager.domain.service.user;

import java.util.List;

import common.manager.app.api.UserApi;
import common.manager.app.rest.request.ChangePasswordRequest;
import common.manager.app.rest.request.CreateUserRequest;
import common.manager.app.rest.request.UserLoginRequest;
import common.manager.app.rest.response.LoginResponse;
import common.manager.domain.exception.GenericException;
import common.manager.domain.service.common.CommonService;

public interface UserService extends CommonService {

    UserApi create(CreateUserRequest request) throws GenericException;

    UserApi get(String username) throws GenericException;

    void enableAccount(String username, String otpCode) throws GenericException;

    LoginResponse login(UserLoginRequest request) throws GenericException;

    void delete(String username);

    List<UserApi> getAll();

    void changePassword(String username, String otpCode, ChangePasswordRequest request) throws GenericException;

}
