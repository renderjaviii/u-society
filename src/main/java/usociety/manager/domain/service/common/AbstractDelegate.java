package usociety.manager.domain.service.common;

import java.util.Optional;

import usociety.manager.app.api.UserApi;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;

public interface AbstractDelegate {

    UserApi getUser(String username) throws GenericException;

    void validateIfUserIsMember(String username,
                                Long groupId,
                                UserGroupStatusEnum status,
                                String errorCode
    ) throws GenericException;

    Optional<UserGroup> getUserGroup(String username, Long groupId) throws GenericException;

    Group getGroup(Long id) throws GenericException;

}
