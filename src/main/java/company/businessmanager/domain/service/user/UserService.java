package company.businessmanager.domain.service.user;

import company.businessmanager.app.api.UserApi;
import company.businessmanager.app.rest.request.CreateUserRequest;
import company.businessmanager.app.rest.request.UserLogin;
import company.businessmanager.domain.exception.GenericException;

public interface UserService {

    void create(CreateUserRequest request) throws GenericException;

    String login(UserLogin request);

    UserApi get(Long userId) throws GenericException;

}
