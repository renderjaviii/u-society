package usociety.manager.domain.provider.user;

import java.util.List;

import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.domain.provider.user.dto.UserDTO;

public interface UserConnector {

    UserDTO create(CreateUserRequest body);

    UserDTO update(UserDTO body);

    UserDTO get(String username);

    UserDTO get(Long id, String username, String email);

    void enableAccount(String username);

    void delete(String username);

    List<UserDTO> getAll();

    void changePassword(String username, ChangePasswordRequest body);

}
