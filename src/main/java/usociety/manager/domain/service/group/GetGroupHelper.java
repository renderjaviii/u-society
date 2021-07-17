package usociety.manager.domain.service.group;

import java.util.List;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;

public interface GetGroupHelper {

    Group getById(Long id) throws GenericException;

    GetGroupResponse get(String username, Long id) throws GenericException;

    List<GroupApi> getByFilters(String name, Long categoryId) throws GenericException;

    GetGroupResponse getBySlug(String user, String slug) throws GenericException;

    List<GroupApi> getAllUserGroups(String username) throws GenericException;

}
