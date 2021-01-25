package usociety.manager.domain.provider.user;

import java.util.List;

import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.service.web.AbstractConnector;

public interface UserConnector extends AbstractConnector {

    UserDTO create(CreateUserRequest body);

    void update(UserDTO body);

    UserDTO get(String username);

    UserDTO get(Long id, String username, String email);

    void enableAccount(String username);

    void delete(String username);

    List<UserDTO> getAll();

    void changePassword(String username, ChangePasswordRequest body);

}
