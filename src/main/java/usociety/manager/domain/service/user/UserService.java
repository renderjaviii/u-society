package usociety.manager.domain.service.user;

import java.util.List;

import javax.mail.MessagingException;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.app.rest.request.LoginRequest;
import usociety.manager.app.rest.request.UpdateUserRequest;
import usociety.manager.app.rest.response.LoginResponse;
import usociety.manager.domain.exception.GenericException;

public interface UserService {

    LoginResponse create(CreateUserRequest request) throws GenericException, MessagingException;

    void verify(String email) throws GenericException;

    UserApi get(String username) throws GenericException;

    UserApi getById(Long id) throws GenericException;

    void enableAccount(String email, String otpCode) throws GenericException;

    LoginResponse login(LoginRequest request) throws GenericException;

    void delete(String username);

    List<UserApi> getAll();

    void changePassword(String username, String otpCode, ChangePasswordRequest request) throws GenericException;

    UserApi update(String username, UpdateUserRequest request) throws GenericException;

}
