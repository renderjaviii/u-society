package common.manager.domain.provider.user;

import java.util.List;

import common.manager.app.api.UserApi;
import common.manager.app.rest.request.CreateUserRequest;
import common.manager.domain.service.web.AbstractConnector;

public interface UserConnector extends AbstractConnector {

    UserApi create(CreateUserRequest body);

    UserApi get(String username);

    UserApi get(String username, String documentNumber, String email, String phoneNumber);

    void enableAccount(String username);

    void delete(String username);

    List<UserApi> getAll();

}
