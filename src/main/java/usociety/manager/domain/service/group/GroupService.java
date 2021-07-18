package usociety.manager.domain.service.group;

import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.app.rest.request.CreateGroupRequest;
import usociety.manager.app.rest.request.UpdateGroupRequest;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;

public interface GroupService {

    GroupApi create(String username, CreateGroupRequest request) throws GenericException;

    GetGroupResponse update(String username, UpdateGroupRequest request) throws GenericException;

    void join(String username, Long id) throws GenericException, MessagingException;

    void updateMembership(String username, Long id, UserGroupApi request) throws GenericException;

    Group get(Long id) throws GenericException;

    Optional<UserGroup> getByIdAndUser(Long id, String username) throws GenericException;

    GetGroupResponse get(String username, Long id) throws GenericException;

    GetGroupResponse getBySlug(String username, String slug) throws GenericException;

    List<GroupApi> getByFilters(String name, Long categoryId) throws GenericException;

    List<GroupApi> getAllUserGroups(String username) throws GenericException;

    Optional<UserGroup> validateIfUserIsMember(String username,
                                               Long groupId,
                                               UserGroupStatusEnum status,
                                               String errorCode
    ) throws GenericException;

}
