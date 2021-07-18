package usociety.manager.domain.service.group;

import java.util.List;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;

public interface GetGroupHelper {

    Group byId(Long id) throws GenericException;

    GetGroupResponse byUserAndId(UserApi user, Long id) throws GenericException;

    GetGroupResponse byUserAndSlug(UserApi user, String slug) throws GenericException;

    List<GroupApi> byFilters(String name, Long categoryId) throws GenericException;

    List<GroupApi> allUserGroups(UserApi user) throws GenericException;

    void validateIfUserIsMember(UserApi user, Long groupId, String errorCode) throws GenericException;

}
