package usociety.manager.domain.service.group.impl;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.mockito.ArgumentMatchers.any;
import static usociety.manager.domain.util.Constants.FORBIDDEN_ACCESS;
import static usociety.manager.domain.util.Constants.GROUP_NOT_FOUND;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.slugify.Slugify;

import usociety.manager.app.api.CategoryApi;
import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.GroupApiFixture;
import usociety.manager.app.api.UserApiFixture;
import usociety.manager.app.rest.request.CreateOrUpdateGroupRequest;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.repository.GroupRepository;
import usociety.manager.domain.repository.UserGroupRepository;
import usociety.manager.domain.service.category.CategoryService;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.email.SendAsyncEmailDelegate;

@RunWith(MockitoJUnitRunner.class)
public class CreateUpdateGroupHelperImplTest {

    @Mock
    private SendAsyncEmailDelegate sendAsyncEmailDelegate;
    @Mock
    private UserGroupRepository userGroupRepository;
    @Mock
    private CloudStorageService cloudStorageService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private Slugify slugify;
    @Mock
    private Clock clock;

    @InjectMocks
    private CreateUpdateGroupHelperImpl subject;

    private CreateOrUpdateGroupRequest createGroupRequest;
    private Category category;
    private Group groupEntity;
    private GroupApi groupApi;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(subject, "clock", clock);
        createGroupRequest = CreateOrUpdateGroupRequest.newBuilder()
                .category(new CategoryApi(1L, EMPTY))
                .objectives(Collections.singletonList("main"))
                .rules(Collections.singletonList("important"))
                .description("Group description")
                .photo("base64Photo")
                .name("Group name")
                .build();

        category = new Category(1L, "Category name");
        groupEntity = Group.newBuilder()
                .id(1122L)
                .category(category)
                .photo("group-image-url")
                .build();

        groupApi = GroupApi.newBuilder()
                .id(1122L)
                .category(new CategoryApi(1L, "Category name"))
                .photo("group-image-url")
                .build();

        Mockito.when(slugify.slugify(any())).thenReturn("group-name");
        Mockito.when(clock.getZone()).thenReturn(ZoneId.of("GMT"));
        Mockito.when(clock.instant()).thenReturn(Instant.now());
    }

    @Test
    public void shouldCreateGroupCorrectly() throws GenericException {
        Mockito.when(categoryService.get(any())).thenReturn(category);
        Mockito.when(cloudStorageService.uploadImage(any())).thenReturn("group-image-url");
        Mockito.when(groupRepository.save(any())).thenReturn(groupEntity);

        GroupApi executed = subject.create(UserApiFixture.value(), createGroupRequest);

        Assert.assertEquals(groupApi, executed);

        InOrder inOrder = Mockito.inOrder(sendAsyncEmailDelegate,
                cloudStorageService,
                userGroupRepository,
                groupRepository,
                categoryService,
                slugify
        );

        inOrder.verify(groupRepository).findByName("Group name");
        inOrder.verify(categoryService).get(1L);
        inOrder.verify(cloudStorageService).uploadImage("base64Photo");
        inOrder.verify(slugify).slugify("Group name");

        ArgumentCaptor<Group> groupArgumentCaptor = ArgumentCaptor.forClass(Group.class);
        inOrder.verify(groupRepository).save(groupArgumentCaptor.capture());
        Group savedGroup = groupArgumentCaptor.getValue();
        Assert.assertEquals(Collections.singletonList("Main"), savedGroup.getObjectives());
        Assert.assertEquals(Collections.singletonList("Important"), savedGroup.getRules());
        Assert.assertEquals("Group description", savedGroup.getDescription());
        Assert.assertEquals(LocalDate.now(clock), savedGroup.getCreatedAt());
        Assert.assertEquals("Group name", savedGroup.getName());
        Assert.assertEquals("group-image-url", savedGroup.getPhoto());
        Assert.assertEquals("group-name", savedGroup.getSlug());
        Assert.assertEquals(category, savedGroup.getCategory());

        ArgumentCaptor<UserGroup> userGroupArgumentCaptor = ArgumentCaptor.forClass(UserGroup.class);
        inOrder.verify(userGroupRepository).save(userGroupArgumentCaptor.capture());
        UserGroup savedUserGroup = userGroupArgumentCaptor.getValue();
        Assert.assertEquals(UserGroupStatusEnum.ACTIVE.getValue(), savedUserGroup.getStatus());
        Assert.assertEquals("Administrator", savedUserGroup.getRole());
        Assert.assertEquals(UserApiFixture.id, savedUserGroup.getUserId());
        Assert.assertEquals(groupEntity, savedUserGroup.getGroup());

        inOrder.verify(sendAsyncEmailDelegate).execute(UserApiFixture.value(), groupEntity, category);
    }

    @Test(expected = GenericException.class)
    public void shouldFailCreatingGroupAndExecuteRollbackCorrectly() throws GenericException {
        Mockito.when(categoryService.get(any())).thenReturn(category);
        String fileUrl = "group-image-url";
        Mockito.when(cloudStorageService.uploadImage(any())).thenReturn(fileUrl);
        Mockito.when(groupRepository.save(any())).thenThrow(new RuntimeException("SQL integrity violation"));

        try {
            subject.create(UserApiFixture.value(), createGroupRequest);
        } catch (GenericException e) {
            Assert.assertEquals("ERROR_CREATING_GROUP", e.getErrorCode());
            Mockito.verify(cloudStorageService).delete(fileUrl);
            throw e;
        }
        Assert.fail();
    }

    @Test(expected = GenericException.class)
    public void shouldFailCreatingGroupIfNameIsAlreadyRegistered() throws GenericException {
        Mockito.when(groupRepository.findByName(any())).thenReturn(Optional.of(groupEntity));

        try {
            subject.create(UserApiFixture.value(), createGroupRequest);
        } catch (GenericException e) {
            Assert.assertEquals("ERROR_CREATING_GROUP", e.getErrorCode());
            Mockito.verifyNoInteractions(sendAsyncEmailDelegate,
                    userGroupRepository,
                    cloudStorageService,
                    categoryService,
                    slugify);
            throw e;
        }
        Assert.fail();
    }

    @Test
    public void shouldUpdateGroupCorrectly() throws GenericException {
        Mockito.when(groupRepository.findById(any())).thenReturn(Optional.of(groupEntity));
        Mockito.when(userGroupRepository.findByGroupIdAndUserIdAndStatus(any(), any(), any()))
                .thenReturn(Optional.of(UserGroup.newBuilder()
                        .status(UserGroupStatusEnum.ACTIVE.getValue())
                        .userId(UserApiFixture.id)
                        .isAdmin(Boolean.TRUE)
                        .group(groupEntity)
                        .role("Financial")
                        .id(31L)
                        .build()));
        Mockito.when(categoryService.get(any())).thenReturn(category);
        Mockito.when(cloudStorageService.uploadImage(any())).thenReturn("new-group-image-url");
        Mockito.when(slugify.slugify(any())).thenReturn("new-group-name");
        Mockito.when(groupRepository.save(any())).thenReturn(groupEntity);

        GroupApi executed = subject.update(UserApiFixture.value(),
                GroupApiFixture.id,
                CreateOrUpdateGroupRequest.newBuilder()
                        .category(new CategoryApi(1L, EMPTY))
                        .objectives(Collections.singletonList("secondary,"))
                        .rules(Collections.singletonList(",,powerful"))
                        .description("Updated description")
                        .photo("newBase64Photo")
                        .name("New group name")
                        .build());

        Assert.assertEquals(groupApi, executed);

        InOrder inOrder = Mockito.inOrder(cloudStorageService,
                userGroupRepository,
                groupRepository,
                categoryService,
                slugify);

        inOrder.verify(groupRepository).findById(GroupApiFixture.id);
        inOrder.verify(groupRepository).findByName("New group name");
        inOrder.verify(userGroupRepository)
                .findByGroupIdAndUserIdAndStatus(GroupApiFixture.id,
                        UserApiFixture.id,
                        UserGroupStatusEnum.ACTIVE.getValue());
        inOrder.verify(categoryService).get(1L);
        inOrder.verify(cloudStorageService).delete("group-image-url");
        inOrder.verify(cloudStorageService).uploadImage("newBase64Photo");
        inOrder.verify(slugify).slugify("New group name");

        ArgumentCaptor<Group> groupArgumentCaptor = ArgumentCaptor.forClass(Group.class);
        inOrder.verify(groupRepository).save(groupArgumentCaptor.capture());
        Group savedGroup = groupArgumentCaptor.getValue();
        Assert.assertEquals(Collections.singletonList("Secondary"), savedGroup.getObjectives());
        Assert.assertEquals(Collections.singletonList("Powerful"), savedGroup.getRules());
        Assert.assertEquals("Updated description", savedGroup.getDescription());
        Assert.assertEquals("new-group-image-url", savedGroup.getPhoto());
        Assert.assertEquals("New group name", savedGroup.getName());
        Assert.assertEquals(LocalDate.now(clock), savedGroup.getUpdatedAt());
        Assert.assertEquals("new-group-name", savedGroup.getSlug());
        Assert.assertEquals(category, savedGroup.getCategory());
    }

    @Test(expected = GenericException.class)
    public void shouldFailUpdatingGroupIfItDoesNotExist() throws GenericException {
        try {
            subject.update(UserApiFixture.value(), GroupApiFixture.id, new CreateOrUpdateGroupRequest());
        } catch (GenericException e) {
            Assert.assertEquals(GROUP_NOT_FOUND, e.getErrorCode());
            Mockito.verifyNoInteractions(userGroupRepository, categoryService, cloudStorageService);
            throw e;
        }
        Assert.fail();
    }

    @Test(expected = GenericException.class)
    public void shouldFailUpdatingGroupIfUserIsNotAnActiveMember() throws GenericException {
        Mockito.when(groupRepository.findById(any())).thenReturn(Optional.of(groupEntity));
        try {
            subject.update(UserApiFixture.value(), GroupApiFixture.id, new CreateOrUpdateGroupRequest());
        } catch (GenericException e) {
            Assert.assertEquals("ERROR_UPDATING_MEMBERSHIP", e.getErrorCode());
            Mockito.verifyNoInteractions(categoryService, cloudStorageService);
            throw e;
        }
        Assert.fail();
    }

    @Test(expected = GenericException.class)
    public void shouldFailUpdatingGroupIfUserIsNotItsAdmin() throws GenericException {
        Mockito.when(groupRepository.findById(any())).thenReturn(Optional.of(groupEntity));
        Mockito.when(userGroupRepository.findByGroupIdAndUserIdAndStatus(any(), any(), any()))
                .thenReturn(Optional.of(UserGroup.newBuilder()
                        .isAdmin(Boolean.FALSE)
                        .id(31L)
                        .build()));
        try {
            subject.update(UserApiFixture.value(), GroupApiFixture.id, new CreateOrUpdateGroupRequest());
        } catch (GenericException e) {
            Assert.assertEquals(FORBIDDEN_ACCESS, e.getErrorCode());
            Mockito.verifyNoInteractions(categoryService, cloudStorageService);
            throw e;
        }
        Assert.fail();
    }

}