package common.manager.domain.provider.user;

import common.manager.app.rest.request.CreateUserRequest;
import common.manager.domain.provider.user.dto.UserDTO;
import common.manager.domain.service.web.AbstractConnector;

public interface UserConnector extends AbstractConnector {

    UserDTO create(CreateUserRequest request);

    UserDTO getByUsername(String username);

    UserDTO get(String username, String documentNumber, String email, String phoneNumber);

    void enableAccount(String username);

    void delete(String username);

}
