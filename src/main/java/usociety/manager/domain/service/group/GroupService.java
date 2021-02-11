package usociety.manager.domain.service.group;

import java.util.List;

import javax.mail.MessagingException;

import com.fasterxml.jackson.core.JsonProcessingException;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.app.rest.request.CreateGroupRequest;
import usociety.manager.app.rest.request.UpdateGroupRequest;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;

public interface GroupService {

    GroupApi create(String username, CreateGroupRequest request) throws GenericException, MessagingException;

    GetGroupResponse get(Long id, String username) throws GenericException;

    Group get(Long id) throws GenericException;

    List<GroupApi> getAllUserGroups(String username) throws GenericException;

    void updateMembership(Long id, UserGroupApi request) throws GenericException;

    GetGroupResponse update(UpdateGroupRequest request, String username)
            throws GenericException, JsonProcessingException;

    List<GroupApi> getByFilters(String name, Long categoryId) throws GenericException;

    void join(Long id, String username) throws GenericException, MessagingException;

    GetGroupResponse getBySlug(String slug, String user) throws GenericException;

}
