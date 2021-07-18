package usociety.manager.domain.service.group.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.app.rest.request.CreateGroupRequest;
import usociety.manager.app.rest.request.UpdateGroupRequest;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.service.group.CreateGroupDelegate;
import usociety.manager.domain.service.group.GetGroupHelper;
import usociety.manager.domain.service.group.GroupMembershipHelper;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.group.UpdateGroupDelegate;
import usociety.manager.domain.service.user.UserService;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupMembershipHelper groupMembershipHelper;
    private final UpdateGroupDelegate updateGroupDelegate;
    private final CreateGroupDelegate createGroupDelegate;
    private final GetGroupHelper getGroupHelper;
    private final UserService userService;

    @Autowired
    public GroupServiceImpl(GroupMembershipHelper groupMembershipHelper,
                            UpdateGroupDelegate updateGroupDelegate,
                            CreateGroupDelegate createGroupDelegate,
                            GetGroupHelper getGroupHelper,
                            UserService userService) {
        this.groupMembershipHelper = groupMembershipHelper;
        this.updateGroupDelegate = updateGroupDelegate;
        this.createGroupDelegate = createGroupDelegate;
        this.getGroupHelper = getGroupHelper;
        this.userService = userService;
    }

    @Override
    public GroupApi create(String username, CreateGroupRequest request)
            throws GenericException {
        return createGroupDelegate.execute(getUser(username), request);
    }

    @Override
    public GetGroupResponse update(String username, UpdateGroupRequest request)
            throws GenericException {
        return updateGroupDelegate.execute(getUser(username), request);
    }

    @Override
    public void join(String username, Long id) throws GenericException {
        groupMembershipHelper.join(getUser(username), id);
    }

    @Override
    public void updateMembership(String username, Long id, UserGroupApi request)
            throws GenericException {
        groupMembershipHelper.update(getUser(username), id, request);
    }

    @Override
    public Group get(Long id) throws GenericException {
        return getGroupHelper.byId(id);
    }

    @Override
    public GetGroupResponse get(String username, Long id) throws GenericException {
        return getGroupHelper.byUserAndId(getUser(username), id);
    }

    @Override
    public GetGroupResponse getBySlug(String username, String slug)
            throws GenericException {
        return getGroupHelper.byUserAndSlug(getUser(username), slug);
    }

    @Override
    public List<GroupApi> getByFilters(String name, Long categoryId)
            throws GenericException {
        return getGroupHelper.byFilters(name, categoryId);
    }

    @Override
    public List<GroupApi> getAllUserGroups(String username)
            throws GenericException {
        return getGroupHelper.allUserGroups(getUser(username));
    }

    @Override
    public void validateIfUserIsMember(String username,
                                       Long groupId,
                                       String errorCode)
            throws GenericException {
        getGroupHelper.validateIfUserIsMember(getUser(username), groupId, errorCode);
    }

    private UserApi getUser(String username) throws GenericException {
        return userService.get(username);
    }

}
