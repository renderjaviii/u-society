package usociety.manager.domain.service.user.impl;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.mockito.ArgumentMatchers.any;
import static usociety.manager.app.api.UserApiFixture.email;
import static usociety.manager.app.api.UserApiFixture.username;

import java.util.Collections;
import java.util.HashSet;
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

import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserApiFixture;
import usociety.manager.app.rest.request.UpdateUserRequest;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.UserCategory;
import usociety.manager.domain.provider.authentication.AuthenticationConnector;
import usociety.manager.domain.provider.user.UserConnector;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.repository.CategoryRepository;
import usociety.manager.domain.repository.UserCategoryRepository;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.otp.OtpService;
import usociety.manager.domain.service.user.CreateUserDelegate;
import usociety.manager.domain.service.user.UpdateUserDelegate;

@RunWith(MockitoJUnitRunner.class)
public class UpdateUserDelegateImplTest {

    private static final String CURRENT_IMAGE_URL = "current-image-url";
    private static final String NEW_IMAGE_URL = "new-image-url";
    private static final String NEW_USER_NAME = "Another Name";

    @Mock
    private AuthenticationConnector authenticationConnector;
    @Mock
    private UserCategoryRepository userCategoryRepository;
    @Mock
    private CreateUserDelegate createUserDelegate;
    @Mock
    private UpdateUserDelegate updateUserDelegate;
    @Mock
    private UserConnector userConnector;
    @Mock
    private MailService mailService;
    @Mock
    private OtpService otpService;
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CloudStorageService cloudStorageService;

    @InjectMocks
    private UpdateUserDelegateImpl subject;

    private UserDTO userDTO;
    private UserApi userApi;

    @Before
    public void setUp() {
        userDTO = UserDTO.newBuilder()
                .name(UserApiFixture.name)
                .photo(CURRENT_IMAGE_URL)
                .id(UserApiFixture.id)
                .username(username)
                .email(email)
                .build();

        userApi = UserApi.newBuilder()
                .id(UserApiFixture.id)
                .photo(NEW_IMAGE_URL)
                .name(NEW_USER_NAME)
                .username(username)
                .email(email)
                .build();

        Mockito.when(userConnector.get(any())).thenReturn(userDTO);
    }

    @Test
    public void shouldUpdateUserCorrectly() throws GenericException {
        Mockito.when(cloudStorageService.upload(any())).thenReturn(NEW_IMAGE_URL);

        Category currentCategory = new Category(UserApiFixture.category.getId(), UserApiFixture.category.getName());
        UserCategory currentUserCategory = new UserCategory(5L, UserApiFixture.id, currentCategory);
        Mockito.when(userCategoryRepository.findAllByUserId(any()))
                .thenReturn(Collections.singletonList(currentUserCategory));

        Category newCategory = new Category(3L, "Category ABC");
        Mockito.when(categoryRepository.findById(any())).thenReturn(Optional.of(newCategory));

        Mockito.when(userConnector.update(any())).thenReturn(userDTO);

        String newImageBase64 = "base64Image";
        UpdateUserRequest request = new UpdateUserRequest(NEW_USER_NAME, newImageBase64, Collections.singleton(3L));
        UserApi executed = subject.execute(username, request);

        Assert.assertEquals(userApi, executed);

        InOrder inOrder = Mockito.inOrder(cloudStorageService,
                userConnector,
                userCategoryRepository,
                categoryRepository);

        inOrder.verify(userConnector).get(UserApiFixture.username);
        inOrder.verify(cloudStorageService).delete(CURRENT_IMAGE_URL);
        inOrder.verify(cloudStorageService).upload(newImageBase64);
        inOrder.verify(userCategoryRepository).findAllByUserId(UserApiFixture.id);

        //Only is required the entity id to determinate if two database registries are the equal
        inOrder.verify(userCategoryRepository)
                .delete(new UserCategory(currentUserCategory.getId(), null, null));

        inOrder.verify(categoryRepository).findById(newCategory.getId());

        //Comparison made in this way due to entity class has auto generated PK
        ArgumentCaptor<UserCategory> argumentCaptor = ArgumentCaptor.forClass(UserCategory.class);
        inOrder.verify(userCategoryRepository).save(argumentCaptor.capture());
        UserCategory argumentCaptorValue = argumentCaptor.getValue();
        Assert.assertEquals(newCategory, argumentCaptorValue.getCategory());
        Assert.assertEquals(UserApiFixture.id, argumentCaptorValue.getUserId());

        inOrder.verify(userConnector).update(userDTO);
    }

    @Test
    public void shouldUpdateOnlyUserNameCorrectly() throws GenericException {
        Category currentCategory = new Category(UserApiFixture.category.getId(), UserApiFixture.category.getName());
        UserCategory currentUserCategory = new UserCategory(5L, UserApiFixture.id, currentCategory);
        Mockito.when(userCategoryRepository.findAllByUserId(any()))
                .thenReturn(Collections.singletonList(currentUserCategory));

        Mockito.when(userConnector.update(any())).thenReturn(userDTO);

        UpdateUserRequest request = new UpdateUserRequest(NEW_USER_NAME, EMPTY,
                new HashSet<>(Collections.singleton(currentCategory.getId())));
        UserApi executed = subject.execute(username, request);

        userApi.setPhoto(CURRENT_IMAGE_URL);
        Assert.assertEquals(userApi, executed);

        InOrder inOrder = Mockito.inOrder(userConnector, userCategoryRepository);

        inOrder.verify(userConnector).get(UserApiFixture.username);
        inOrder.verify(userCategoryRepository).findAllByUserId(UserApiFixture.id);
        inOrder.verify(userConnector).update(userDTO);

        Mockito.verifyNoInteractions(cloudStorageService, categoryRepository);
        Mockito.verify(userCategoryRepository, Mockito.never())
                .delete(new UserCategory(currentUserCategory.getId(), null, null));
    }

}