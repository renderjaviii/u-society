package usociety.manager.domain.service.common.impl;

import static usociety.manager.domain.util.Constants.USER_NOT_FOUND_ERROR_CODE;

import java.time.Clock;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.UserApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.service.common.AbstractService;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.user.UserService;
import usociety.manager.domain.util.mapper.CustomObjectMapper;

@Primary
@Service
public class AbstractServiceImpl implements AbstractService {

    @Autowired
    private GroupService groupService;

    @Autowired
    protected Clock clock;
    @Autowired
    protected UserService userService;
    @Autowired
    protected CustomObjectMapper objectMapper;

    protected AbstractServiceImpl() {
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

    @Override
    public Group getGroup(Long id) throws GenericException {
        return groupService.get(id);
    }

    @Override
    public void validateIfUserIsMember(String username,
                                       Long groupId,
                                       String errorCode) throws GenericException {
        groupService.validateIfUserIsMember(username, groupId, errorCode);
    }

}
