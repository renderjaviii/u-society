package usociety.manager.domain.service.group.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.app.rest.request.CreateGroupRequest;
import usociety.manager.app.rest.request.UpdateGroupRequest;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.service.common.impl.CommonServiceImpl;
import usociety.manager.domain.service.group.CreateGroupDelegate;
import usociety.manager.domain.service.group.GetGroupHelper;
import usociety.manager.domain.service.group.GroupMembershipHelper;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.group.UpdateGroupDelegate;

@Service
public class GroupServiceImpl extends CommonServiceImpl implements GroupService {

    private final GroupMembershipHelper groupMembershipHelper;
    private final UpdateGroupDelegate updateGroupDelegate;
    private final CreateGroupDelegate createGroupDelegate;
    private final GetGroupHelper getGroupHelper;

    @Autowired
    public GroupServiceImpl(GroupMembershipHelper groupMembershipHelper,
                            UpdateGroupDelegate updateGroupDelegate,
                            CreateGroupDelegate createGroupDelegate,
                            GetGroupHelper getGroupHelper) {
        this.groupMembershipHelper = groupMembershipHelper;
        this.updateGroupDelegate = updateGroupDelegate;
        this.createGroupDelegate = createGroupDelegate;
        this.getGroupHelper = getGroupHelper;
    }

    @Override
    public GroupApi create(String username, CreateGroupRequest request)
            throws GenericException {
        return createGroupDelegate.execute(username, request);
    }

    @Override
    public GetGroupResponse update(String username, UpdateGroupRequest request)
            throws GenericException {
        return updateGroupDelegate.execute(username, request);
    }

    @Override
    public void join(Long id, String username) throws GenericException {
        groupMembershipHelper.join(id, username);
    }

    @Override
    public void updateMembership(Long id, UserGroupApi request)
            throws GenericException {
        groupMembershipHelper.update(id, request);
    }

    @Override
    public Group get(Long id) throws GenericException {
        return getGroupHelper.getById(id);
    }

    @Override
    public GetGroupResponse get(String username, Long id) throws GenericException {
        return getGroupHelper.get(username, id);
    }

    @Override
    public List<GroupApi> getByFilters(String name, Long categoryId)
            throws GenericException {
        return getGroupHelper.getByFilters(name, categoryId);
    }

    @Override
    public GetGroupResponse getBySlug(String user, String slug)
            throws GenericException {
        return getGroupHelper.getBySlug(user, slug);
    }

    @Override
    public List<GroupApi> getAllUserGroups(String username)
            throws GenericException {
        return getGroupHelper.getAllUserGroups(username);
    }

}
