package usociety.manager.domain.service.group.impl;

import static java.lang.Boolean.FALSE;
import static org.mockito.ArgumentMatchers.any;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import usociety.manager.app.api.CategoryApi;
import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.GroupApiFixture;
import usociety.manager.app.api.UserApiFixture;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.app.rest.request.CreateOrUpdateGroupRequest;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.service.group.CreateUpdateGroupHelper;
import usociety.manager.domain.service.group.GetGroupHelper;
import usociety.manager.domain.service.group.GroupMembershipHelper;
import usociety.manager.domain.service.user.UserService;

@RunWith(MockitoJUnitRunner.class)
public class GroupServiceImplTest {

    @Mock
    private CreateUpdateGroupHelper createUpdateGroupHelper;
    @Mock
    private GroupMembershipHelper groupMembershipHelper;
    @Mock
    private GetGroupHelper getGroupHelper;
    @Mock
    private UserService userService;

    @InjectMocks
    private GroupServiceImpl subject;

    private CreateOrUpdateGroupRequest createOrUpdateGroupRequest;
    private GetGroupResponse getGroupResponse;

    @Before
    public void setUp() throws GenericException {
        ReflectionTestUtils.setField(subject, "userService", userService);
        Mockito.when(userService.get(any())).thenReturn(UserApiFixture.value());

        createOrUpdateGroupRequest = CreateOrUpdateGroupRequest.newBuilder()
                .category(new CategoryApi(1L, "Category name"))
                .objectives(Collections.singletonList("Main"))
                .rules(Collections.singletonList("Important"))
                .description("Group description")
                .name("Group name")
                .build();

        getGroupResponse = GetGroupResponse.newBuilder()
                .membershipStatus(UserGroupStatusEnum.PENDING)
                .group(GroupApiFixture.value())
                .isAdmin(FALSE)
                .build();
    }

    @Test
    public void shouldCreateGroupCorrectly() throws GenericException {
        Mockito.when(createUpdateGroupHelper.create(any(), any())).thenReturn(GroupApiFixture.value());

        GroupApi executed = subject.create(UserApiFixture.username, createOrUpdateGroupRequest);

        Assert.assertEquals(GroupApiFixture.value(), executed);
        Mockito.verify(createUpdateGroupHelper).create(UserApiFixture.value(), createOrUpdateGroupRequest);
        Mockito.verify(userService).get(UserApiFixture.username);
    }

    @Test
    public void shouldUpdateGroupCorrectly() throws GenericException {
        Mockito.when(createUpdateGroupHelper.update(any(), any(), any())).thenReturn(GroupApiFixture.value());

        GroupApi executed = subject.update(UserApiFixture.username, GroupApiFixture.id, createOrUpdateGroupRequest);

        Assert.assertEquals(GroupApiFixture.value(), executed);
        Mockito.verify(createUpdateGroupHelper)
                .update(UserApiFixture.value(), GroupApiFixture.id, createOrUpdateGroupRequest);
        Mockito.verify(userService).get(UserApiFixture.username);
    }

    @Test
    public void shouldJoinGroupCorrectly() throws GenericException {
        subject.join(UserApiFixture.username, GroupApiFixture.id);
        Mockito.verify(groupMembershipHelper).join(UserApiFixture.value(), GroupApiFixture.id);
        Mockito.verify(userService).get(UserApiFixture.username);
    }

    @Test
    public void shouldUpdateGroupMembershipCorrectly() throws GenericException {
        UserGroupApi request = UserGroupApi.newBuilder()
                .member(UserApiFixture.value())
                .status(UserGroupStatusEnum.ACTIVE)
                .build();

        subject.updateMembership(UserApiFixture.username, GroupApiFixture.id, request);

        Mockito.verify(groupMembershipHelper).update(UserApiFixture.value(), GroupApiFixture.id, request);
        Mockito.verify(userService).get(UserApiFixture.username);
    }

    @Test
    public void shouldGetGroupByIdCorrectly() throws GenericException {
        Group group = Group.newBuilder().id(21L).name("Group name").build();
        Mockito.when(getGroupHelper.byId(any())).thenReturn(group);

        Group executed = subject.get(GroupApiFixture.id);

        Assert.assertEquals(group, executed);
        Mockito.verify(getGroupHelper).byId(GroupApiFixture.id);
        Mockito.verifyNoInteractions(userService);
    }

    @Test
    public void shouldGetGroupByIdAndUserCorrectly() throws GenericException {
        Mockito.when(getGroupHelper.byUserAndId(any(), any())).thenReturn(getGroupResponse);

        GetGroupResponse executed = subject.get(UserApiFixture.username, GroupApiFixture.id);

        Assert.assertEquals(getGroupResponse, executed);
        Mockito.verify(getGroupHelper).byUserAndId(UserApiFixture.value(), GroupApiFixture.id);
        Mockito.verify(userService).get(UserApiFixture.username);
    }

    @Test
    public void shouldGetGroupBySlugCorrectly() throws GenericException {
        Mockito.when(getGroupHelper.byUserAndSlug(any(), any())).thenReturn(getGroupResponse);

        GetGroupResponse executed = subject.getBySlug(UserApiFixture.username, GroupApiFixture.slug);
        Assert.assertEquals(getGroupResponse, executed);
        Mockito.verify(getGroupHelper).byUserAndSlug(UserApiFixture.value(), GroupApiFixture.slug);
        Mockito.verify(userService).get(UserApiFixture.username);
    }

    @Test
    public void shouldGetByFiltersGroupCorrectly() throws GenericException {
        Mockito.when(getGroupHelper.byFilters(any(), any()))
                .thenReturn(Collections.singletonList(GroupApiFixture.value()));

        String groupName = "Group name";
        long categoryId = 41L;
        List<GroupApi> executed = subject.getByFilters(groupName, categoryId);

        Assert.assertEquals(Collections.singletonList(GroupApiFixture.value()), executed);
        Mockito.verify(getGroupHelper).byFilters(groupName, categoryId);
        Mockito.verifyNoInteractions(userService);
    }

    @Test
    public void shouldGetAllUserGroupsCorrectly() throws GenericException {
        Mockito.when(getGroupHelper.allUserGroups(any()))
                .thenReturn(Collections.nCopies(3, GroupApiFixture.value()));

        List<GroupApi> executed = subject.getAllUserGroups(UserApiFixture.username);

        Assert.assertEquals(Collections.nCopies(3, GroupApiFixture.value()), executed);
        Mockito.verify(getGroupHelper).allUserGroups(UserApiFixture.value());
        Mockito.verify(userService).get(UserApiFixture.username);
    }

    @Test
    public void shouldValidateIfUserIsMemberGroupCorrectly() throws GenericException {
        String customError = "Custom error";
        subject.validateIfUserIsMember(UserApiFixture.username, GroupApiFixture.id, customError);

        Mockito.verify(getGroupHelper)
                .validateIfUserIsMember(UserApiFixture.value(), GroupApiFixture.id, customError);
        Mockito.verify(userService).get(UserApiFixture.username);
    }

}