package usociety.manager.domain.service.group.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static usociety.manager.domain.util.Constants.GROUP_NOT_FOUND;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import usociety.manager.app.api.GroupApiFixture;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserApiFixture;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.enums.UserTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.repository.GroupRepository;
import usociety.manager.domain.repository.UserGroupRepository;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.user.UserService;

@RunWith(MockitoJUnitRunner.class)
public class GroupMembershipHelperImplTest {

    private static final String APPLICATION_DOMAIN = "www.example.com";

    @Mock
    private UserGroupRepository userGroupRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private MailService mailService;
    @Mock
    private UserService userService;

    @InjectMocks
    private GroupMembershipHelperImpl subject;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(subject, "applicationDomain", APPLICATION_DOMAIN);
    }

    @Test
    public void shouldUpdateUserGroupMembershipCorrectly() throws GenericException {
        Mockito.when(userGroupRepository.findByGroupIdAndUserIdAndIsAdmin(any(), any(), anyBoolean()))
                .thenReturn(Optional.of(new UserGroup()));

        Long memberUserGroupId = 31L;
        UserGroup memberUserGroup = UserGroup.newBuilder()
                .status(UserGroupStatusEnum.PENDING.getValue())
                .group(Group.newBuilder().id(GroupApiFixture.id).build())
                .userId(UserApiFixture.id)
                .isAdmin(Boolean.FALSE)
                .id(memberUserGroupId)
                .build();

        Mockito.when(userGroupRepository.findByGroupIdAndUserId(any(), any()))
                .thenReturn(Optional.of(memberUserGroup));

        Long adminUserId = 123L;
        subject.update(UserApi.newBuilder().id(adminUserId).build(),
                GroupApiFixture.id,
                UserGroupApi.newBuilder()
                        .member(UserApiFixture.value())
                        .status(UserGroupStatusEnum.ACTIVE)
                        .build());

        Mockito.verify(userGroupRepository)
                .findByGroupIdAndUserIdAndIsAdmin(GroupApiFixture.id, adminUserId, Boolean.TRUE);
        Mockito.verify(userGroupRepository)
                .findByGroupIdAndUserId(GroupApiFixture.id, UserApiFixture.id);

        ArgumentCaptor<UserGroup> argumentCaptor = ArgumentCaptor.forClass(UserGroup.class);
        Mockito.verify(userGroupRepository).save(argumentCaptor.capture());
        UserGroup savedUserGroup = argumentCaptor.getValue();
        Assert.assertEquals(UserGroupStatusEnum.ACTIVE.getValue(), savedUserGroup.getStatus());
        Assert.assertEquals(UserTypeEnum.STANDARD.getValue(), savedUserGroup.getRole());
        Assert.assertEquals(UserApiFixture.id, savedUserGroup.getUserId());
        Assert.assertEquals(memberUserGroupId, savedUserGroup.getId());
    }

    @Test(expected = GenericException.class)
    public void shouldFailUpdatingUserGroupMembershipIfIsNotAdmin() throws GenericException {
        try {
            subject.update(UserApi.newBuilder().id(123L).build(), GroupApiFixture.id, new UserGroupApi());
        } catch (GenericException e) {
            Assert.assertEquals("You are not allowed to perform this operation", e.getMessage());
            Assert.assertEquals("ERROR_UPDATING_MEMBERSHIP", e.getErrorCode());
            throw e;
        }
        Assert.fail();
    }

    @Test(expected = GenericException.class)
    public void shouldFailUpdatingUserGroupMembershipIfMemberIsNotSent() throws GenericException {
        Mockito.when(userGroupRepository.findByGroupIdAndUserIdAndIsAdmin(any(), any(), anyBoolean()))
                .thenReturn(Optional.of(new UserGroup()));
        try {
            subject.update(UserApi.newBuilder().id(123L).build(),
                    GroupApiFixture.id,
                    UserGroupApi.newBuilder()
                            .member(new UserApi())
                            .build());
        } catch (GenericException e) {
            Assert.assertEquals("ERROR_UPDATING_MEMBERSHIP", e.getErrorCode());
            Assert.assertEquals("Member id must be sent", e.getMessage());
            throw e;
        }
        Assert.fail();
    }

    @Test(expected = GenericException.class)
    public void shouldFailUpdatingUserGroupMembershipIfUserIsNotMember() throws GenericException {
        Mockito.when(userGroupRepository.findByGroupIdAndUserIdAndIsAdmin(any(), any(), anyBoolean()))
                .thenReturn(Optional.of(new UserGroup()));
        try {
            subject.update(UserApi.newBuilder().id(123L).build(),
                    GroupApiFixture.id,
                    UserGroupApi.newBuilder()
                            .member(UserApiFixture.value())
                            .status(UserGroupStatusEnum.ACTIVE)
                            .build());
        } catch (GenericException e) {
            Assert.assertEquals("ERROR_UPDATING_MEMBERSHIP", e.getErrorCode());
            Assert.assertEquals("User is not member", e.getMessage());
            throw e;
        }
        Assert.fail();
    }

    @Test
    public void shouldJoinGroupCorrectly() throws GenericException {
        Mockito.when(groupRepository.findById(any()))
                .thenReturn(Optional.of(Group.newBuilder()
                        .name(GroupApiFixture.name)
                        .id(GroupApiFixture.id)
                        .build()));

        String adminUserEmail = "admin@test.com";
        String adminUserName = "Admin name";
        Long adminUserId = 987L;
        Mockito.when(userGroupRepository.findByGroupIdAndIsAdmin(any(), anyBoolean()))
                .thenReturn(Optional.of(UserGroup.newBuilder().userId(adminUserId).build()));
        Mockito.when(userService.getById(any()))
                .thenReturn(UserApi.newBuilder()
                        .email(adminUserEmail)
                        .name(adminUserName)
                        .id(adminUserId)
                        .build());

        subject.join(UserApiFixture.value(), GroupApiFixture.id);

        Mockito.verify(groupRepository).findById(GroupApiFixture.id);
        Mockito.verify(userGroupRepository).findByGroupIdAndUserId(GroupApiFixture.id, UserApiFixture.id);
        Mockito.verify(userGroupRepository).findByGroupIdAndIsAdmin(GroupApiFixture.id, Boolean.TRUE);
        Mockito.verify(userService).getById(adminUserId);

        ArgumentCaptor<UserGroup> argumentCaptor = ArgumentCaptor.forClass(UserGroup.class);
        Mockito.verify(userGroupRepository).save(argumentCaptor.capture());
        UserGroup savedUserGroup = argumentCaptor.getValue();
        Assert.assertEquals(UserGroupStatusEnum.PENDING.getValue(), savedUserGroup.getStatus());
        Assert.assertEquals(UserApiFixture.id, savedUserGroup.getUserId());
        Assert.assertEquals(Boolean.FALSE, savedUserGroup.isAdmin());

        Mockito.verify(mailService).send(adminUserEmail,
                "<html><body><h3>Hola " + adminUserName + ".</h3><p>" + UserApiFixture.name +
                        " ha solicitado unirse a tu grupo: <u>" + GroupApiFixture.name + "</u></p>" +
                        "<p>¡Dirígite a <a href='" + APPLICATION_DOMAIN +
                        "'>U - Society</a> y permítele ingresar!</p></body></html>",
                Boolean.TRUE);
    }

    @Test(expected = GenericException.class)
    public void shouldFailJoiningGroupIfItDoesNotExist() throws GenericException {
        try {
            subject.join(UserApiFixture.value(), GroupApiFixture.id);
        } catch (GenericException e) {
            Assert.assertEquals(GROUP_NOT_FOUND, e.getErrorCode());
            throw e;
        }
        Assert.fail();
    }

    @Test(expected = GenericException.class)
    public void shouldFailJoiningGroupIfUserHasAlreadyRequestFor() throws GenericException {
        Mockito.when(groupRepository.findById(any()))
                .thenReturn(Optional.of(Group.newBuilder()
                        .name(GroupApiFixture.name)
                        .id(GroupApiFixture.id)
                        .build()));

        Mockito.when(userGroupRepository.findByGroupIdAndUserId(any(), any()))
                .thenReturn(Optional.of(new UserGroup()));

        try {
            subject.join(UserApiFixture.value(), GroupApiFixture.id);
        } catch (GenericException e) {
            Assert.assertEquals("User has already required to join", e.getMessage());
            Assert.assertEquals("ERROR_JOINING_GROUP", e.getErrorCode());
            throw e;
        }
        Assert.fail();
    }

}