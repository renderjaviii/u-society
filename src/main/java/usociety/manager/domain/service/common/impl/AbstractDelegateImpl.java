package usociety.manager.domain.service.common.impl;

import static usociety.manager.domain.util.Constant.USER_NOT_FOUND_ERROR_CODE;

import java.time.Clock;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import usociety.manager.app.api.UserApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.common.AbstractDelegate;
import usociety.manager.domain.service.user.UserService;
import usociety.manager.domain.util.mapper.CustomObjectMapper;

@Primary
@Component
public class AbstractDelegateImpl implements AbstractDelegate {

    @Autowired
    protected Clock clock;

    @Autowired
    protected UserService userService;

    @Autowired
    protected CustomObjectMapper objectMapper;

    protected AbstractDelegateImpl() {
        super();
    }

    @Override
    public UserApi getUser(String username) throws GenericException {
        UserApi user = userService.get(username);
        if (Objects.isNull(user)) {
            String errorMessage = String.format("User with username: %s does not exist", username);
            throw new GenericException(errorMessage, USER_NOT_FOUND_ERROR_CODE);
        }
        return user;
    }

}
