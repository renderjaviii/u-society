package usociety.manager.domain.service.common.impl;

import static usociety.manager.domain.util.Constant.GETTING_GROUP_ERROR_CODE;
import static usociety.manager.domain.util.Constant.USER_NOT_FOUND_ERROR_CODE;

import java.time.Clock;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import usociety.manager.app.api.UserApi;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.repository.GroupRepository;
import usociety.manager.domain.repository.UserGroupRepository;
import usociety.manager.domain.service.common.AbstractDelegate;
import usociety.manager.domain.service.user.UserService;

@Component
public abstract class AbstractDelegateImpl implements AbstractDelegate {

    @Autowired
    protected Clock clock;

    @Autowired
    protected UserService userService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    protected AbstractDelegateImpl() {
        super();
    }

    @Override
    public UserApi getUser(String username) throws GenericException {
        UserApi user = userService.get(username);
        if (Objects.isNull(user)) {
            String errorMessage = String.format("Usuario con alias: %s no existe.", username);
            throw new GenericException(errorMessage, USER_NOT_FOUND_ERROR_CODE);
        }
        return user;
    }

    @Override
    public void validateIfUserIsMember(String username, Long groupId, UserGroupStatusEnum status, String errorCode)
            throws GenericException {
        UserApi user = getUser(username);

        Optional<UserGroup> optionalUserGroup = userGroupRepository
                .findByGroupIdAndUserIdAndStatus(groupId, user.getId(), status.getCode());
        if (StringUtils.isNotEmpty(errorCode) && !optionalUserGroup.isPresent()) {
            throw new GenericException("El usuario no es miembro activo del grupo.", errorCode);
        }
    }

    @Override
    public Optional<UserGroup> getUserGroup(String username, Long groupId) throws GenericException {
        UserApi user = getUser(username);
        return userGroupRepository.findByGroupIdAndUserId(groupId, user.getId());
    }

    @Override
    public Group getGroup(Long id) throws GenericException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (!optionalGroup.isPresent()) {
            throw new GenericException(String.format("Grupo con id: %s no existe.", id), GETTING_GROUP_ERROR_CODE);
        }
        return optionalGroup.get();
    }

}
