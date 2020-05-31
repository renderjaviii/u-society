package common.manager.domain.provider.user;

import java.util.List;

import common.manager.app.rest.request.ChangePasswordRequest;
import common.manager.app.rest.request.CreateUserRequest;
import common.manager.domain.provider.user.dto.UserDTO;
import common.manager.domain.service.web.AbstractConnector;

public interface UserConnector extends AbstractConnector {

    UserDTO create(CreateUserRequest body);

    UserDTO get(String username);

    UserDTO get(String username, String documentNumber, String email, String phoneNumber);

    void enableAccount(String username);

    void delete(String username);

    List<UserDTO> getAll();

    void changePassword(String username, ChangePasswordRequest body);

}
