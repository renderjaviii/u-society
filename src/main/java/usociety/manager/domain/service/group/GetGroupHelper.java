package usociety.manager.domain.service.group;

import java.util.List;
import java.util.Optional;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;

public interface GetGroupHelper {

    Group byId(Long id) throws GenericException;

    Optional<UserGroup> byIdAndUser(Long id, String username) throws GenericException;

    GetGroupResponse byUserAndId(String username, Long id) throws GenericException;

    GetGroupResponse byUserAndSlug(String username, String slug) throws GenericException;

    List<GroupApi> byFilters(String name, Long categoryId) throws GenericException;

    List<GroupApi> allUserGroups(String username) throws GenericException;

    Optional<UserGroup> validateIfUserIsMember(String username,
                                               Long groupId,
                                               UserGroupStatusEnum status,
                                               String errorCode
    ) throws GenericException;

}
