package usociety.manager.domain.service.common;

import java.util.List;
import java.util.Optional;

import usociety.manager.app.api.UserApi;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;

public interface AbstractDelegate {

    Group getGroup(Long id) throws GenericException;

    Optional<UserGroup> getUserGroup(UserApi user,
                                     Long groupId,
                                     List<UserGroupStatusEnum> statuses)
            throws GenericException;

}
