package common.manager.domain.provider.user;

import common.manager.app.rest.request.CreateUserRequest;
import common.manager.domain.provider.user.dto.UserDTO;

public interface UserConnector {

    UserDTO create(CreateUserRequest request);

    UserDTO getByUsername(String username);

    UserDTO get(String username, String documentNumber, String email, String phoneNumber);

    void enableAccount(String username);

}
