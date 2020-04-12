package common.manager.domain.service.oauth;

import common.manager.domain.model.User;

public interface PasswordManager {

    User checkPassword(User user, String rawPassword);

}
