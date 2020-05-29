package common.manager.domain.provider.user.impl;

import org.springframework.stereotype.Component;

import common.manager.app.rest.request.CreateUserRequest;
import common.manager.domain.provider.user.UserConnector;
import common.manager.domain.provider.user.dto.UserDTO;

@Component
public class UserConnectorImpl implements UserConnector {

    @Override
    public UserDTO create(CreateUserRequest request) {
        return null;
    }

    @Override
    public UserDTO getByUsername(String username) {
        return null;
    }

    @Override
    public UserDTO get(String username, String documentNumber, String email, String phoneNumber) {
        return null;
    }

    @Override
    public void enableAccount(String username) {
    }

}
