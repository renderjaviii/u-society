package usociety.manager.domain.service.user;

import java.util.List;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.app.rest.request.UserLoginRequest;
import usociety.manager.app.rest.response.LoginResponse;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.common.CommonService;

public interface UserService extends CommonService {

    UserApi create(CreateUserRequest request) throws GenericException;

    UserApi get(String username) throws GenericException;

    void enableAccount(String username, String otpCode) throws GenericException;

    LoginResponse login(UserLoginRequest request) throws GenericException;

    void delete(String username);

    List<UserApi> getAll();

    void changePassword(String username, String otpCode, ChangePasswordRequest request) throws GenericException;

}
