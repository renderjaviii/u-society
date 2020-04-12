package common.manager.domain.service.oauth;

import common.manager.domain.model.User;

public interface PasswordManager {

    String encode(String rawPassword);

    User checkPassword(User user, String rawPassword);

}
