package common.manager.domain.service.user;

import common.manager.app.api.UserApi;
import common.manager.app.rest.request.CreateUserRequest;
import common.manager.app.rest.request.UserLogin;
import common.manager.domain.exception.GenericException;

public interface UserService {

    void create(CreateUserRequest request) throws GenericException;

    String login(UserLogin request);

    UserApi get(Long userId) throws GenericException;

}
