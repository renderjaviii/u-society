package usociety.manager.domain.service.user.impl;

import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import usociety.manager.app.api.CategoryApi;
import usociety.manager.app.api.OtpApi;
import usociety.manager.app.api.TokenApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.app.rest.request.UpdateUserRequest;
import usociety.manager.app.rest.request.UserLoginRequest;
import usociety.manager.app.rest.response.LoginResponse;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.exception.WebException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.UserCategory;
import usociety.manager.domain.provider.authentication.AuthenticationConnector;
import usociety.manager.domain.provider.user.UserConnector;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.repository.CategoryRepository;
import usociety.manager.domain.repository.UserCategoryRepository;
import usociety.manager.domain.service.aws.s3.CloudStorageService;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.otp.OtpService;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    @Mock
    private AuthenticationConnector authenticationConnector;
    @Mock
    private UserCategoryRepository userCategoryRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserConnector userConnector;
    @Mock
    private MailService mailService;
    @Mock
    private OtpService otpService;
    @Mock
    private CloudStorageService cloudStorageService;

    @InjectMocks
    private UserServiceImpl subject;

    UserDTO user;

    @Before
    public void setUp() throws GenericException {
        ReflectionTestUtils.setField(subject, "validateOtp", TRUE);

        user = UserDTO.newBuilder()
                .username(USERNAME)
                .name("First Name")
                .email(EMAIL)
                .id(1L)
                .build();
        when(userConnector.get(any(), any(), any())).thenReturn(user);
        when(userConnector.get(any())).thenReturn(user);

        when(cloudStorageService.upload(any())).thenReturn("urlImage");
        when((userConnector.create(any()))).thenReturn(user);
    }

    @Test
    public void shouldCreateUserCorrectly() throws GenericException {
        when(userConnector.get(any(), any(), any())).thenReturn(null);
        MockMultipartFile multipartFile = new MockMultipartFile("fileName", new byte[10]);
        CreateUserRequest createUserRequest = CreateUserRequest.newBuilder()
                .username(USERNAME)
                .password("pass")
                .email(EMAIL)
                .otpCode("otp")
                .name("name")
                .build();

        UserApi executed = subject.create(createUserRequest, multipartFile);
        assertEquals(UserApi.newBuilder()
                        .username(USERNAME)
                        .name("First Name")
                        .email(EMAIL)
                        .id(1L).build(),
                executed);

        InOrder inOrder = Mockito.inOrder(otpService, userConnector, cloudStorageService, userConnector, mailService);
        inOrder.verify(otpService).validate(USERNAME, "otp");
        inOrder.verify(userConnector).get(null, USERNAME, EMAIL);
        inOrder.verify(cloudStorageService).upload(multipartFile);
        inOrder.verify(userConnector).create(createUserRequest);
        inOrder.verify(mailService).send(EMAIL,
                "<html><body>" +
                        "<h3>¡Hola <u>Name</u>!</h3>" +
                        "<p>Bienvenido a <b>U Society</b>, logueate y descrubre todo lo que tenemos para ti.</p>" +
                        "</html></body>",
                TRUE);

    }

    @Test(expected = GenericException.class)
    public void shouldNotCreateUserIfOtpCodeIsInvalid() throws GenericException {
        doThrow(new GenericException("Invalid OTP")).when(otpService).validate(any(), any());
        try {
            subject.create(CreateUserRequest.newBuilder()
                            .username(USERNAME)
                            .otpCode("otp")
                            .build(),
                    null);
        } catch (GenericException e) {
            assertEquals("Invalid OTP", e.getMessage());
            verifyNoInteractions(userConnector);
            verifyNoInteractions(cloudStorageService);
            verifyNoInteractions(mailService);
            throw e;
        }
        fail();
    }

    @Test(expected = GenericException.class)
    public void shouldNotCreateUserIfThisAlreadyExists() throws GenericException {
        try {
            subject.create(new CreateUserRequest(), null);
        } catch (GenericException e) {
            assertEquals("USER_ALREADY_EXISTS", e.getErrorCode());
            verifyNoInteractions(cloudStorageService);
            verifyNoInteractions(mailService);
            throw e;
        }
        fail();
    }

    @Test(expected = GenericException.class)
    public void shouldNotCreateUserIfGettingFailsByConnectionProblems() throws GenericException {
        when(userConnector.get(any(), any(), any())).thenThrow(new WebException("Connection time out."));
        try {
            subject.create(new CreateUserRequest(), null);
        } catch (WebException e) {
            assertEquals("Connection time out.", e.getMessage());
            verifyNoInteractions(cloudStorageService);
            verifyNoInteractions(mailService);
            throw e;
        }
        fail();
    }

    @Test(expected = GenericException.class)
    public void shouldNotCreateUserIfImageCannotBeUpload() throws GenericException {
        when(userConnector.get(any(), any(), any())).thenReturn(null);
        when(cloudStorageService.upload(any())).thenThrow(new GenericException("Image could not be uploaded."));
        try {
            subject.create(CreateUserRequest.newBuilder()
                            .username(USERNAME)
                            .otpCode("otp")
                            .build(),
                    null);
        } catch (GenericException e) {
            assertEquals("Image could not be uploaded.", e.getMessage());
            verifyNoInteractions(mailService);
            throw e;
        }
        fail();
    }

    @Test
    public void shouldCreateSendOtpForLogUpCorrectly() throws GenericException {
        when(userConnector.get(any(), any(), any())).thenReturn(null);
        when(otpService.create(any(), any())).thenReturn(OtpApi.newBuilder()
                .otpCode("otp")
                .build());
        subject.verify(USERNAME, EMAIL);
        verify(userConnector).get(null, USERNAME, EMAIL);
        verify(otpService).create(USERNAME, EMAIL);
        verify(mailService).sendOtp(EMAIL, "otp");
    }

    @Test(expected = GenericException.class)
    public void shouldDeleteImageIfUserCreationFails() throws GenericException {
        when(userConnector.get(any(), any(), any())).thenReturn(null);
        MockMultipartFile multipartFile = new MockMultipartFile("fileName", new byte[10]);
        CreateUserRequest createUserRequest = CreateUserRequest.newBuilder()
                .username(USERNAME)
                .password("pass")
                .email(EMAIL)
                .otpCode("otp")
                .name("name")
                .build();

        when(cloudStorageService.upload(any())).thenReturn("newImageUrl");

        when(userConnector.create(any())).thenThrow(new WebException("Error."));
        try {
            subject.create(createUserRequest, multipartFile);
        } catch (GenericException e) {
            assertEquals("USER_NOT_CREATED_ERROR", e.getErrorCode());
            verify(cloudStorageService).delete("newImageUrl");
            throw e;
        }
        fail();
    }

    @Test
    public void shouldGetUserByUsernameCorrectly() {
        UserApi executed = subject.get(USERNAME);
        assertEquals(UserApi.newBuilder()
                        .username(USERNAME)
                        .name("First Name")
                        .email(EMAIL)
                        .id(1L)
                        .build(),
                executed);
        verify(userConnector).get(USERNAME);
    }

    @Test
    public void shouldGetUserByIdCorrectly() {
        UserApi executed = subject.getById(1L);
        assertEquals(UserApi.newBuilder()
                        .username(USERNAME)
                        .name("First Name")
                        .email(EMAIL)
                        .id(1L)
                        .build(),
                executed);
        verify(userConnector).get(1L, null, null);
    }

    @Test
    public void shouldEnableUserCorrectly() throws GenericException {
        subject.enableAccount(USERNAME, "otp");
        verify(otpService).validate(USERNAME, "otp");
        verify(userConnector).enableAccount(USERNAME);
    }

    @Test
    public void shouldDoLoginCorrectly() {
        UserLoginRequest userLoginRequest = UserLoginRequest.newBuilder()
                .username(USERNAME)
                .password("pass")
                .build();
        when(authenticationConnector.login(userLoginRequest)).thenReturn(TokenApi.newBuilder()
                .accessToken("accessToken")
                .expiresIn("expiresIn")
                .jti("jti")
                .scope("scope")
                .tokenType("tokenType")
                .build());
        when(userCategoryRepository.findAllByUserId(any()))
                .thenReturn(Collections.singletonList(UserCategory.newBuilder()
                        .category(Category.newBuilder()
                                .id(2L)
                                .name("categoryName")
                                .build())
                        .build()));

        LoginResponse executed = subject.login(userLoginRequest);
        assertEquals(new LoginResponse(UserApi.newBuilder()
                        .categoryList(Collections.singletonList(new CategoryApi(2L, "categoryName")))
                        .username(USERNAME)
                        .name("First Name")
                        .email(EMAIL)
                        .id(1L)
                        .build(),
                        TokenApi.newBuilder()
                                .accessToken("accessToken")
                                .expiresIn("expiresIn")
                                .jti("jti")
                                .scope("scope")
                                .tokenType("tokenType")
                                .build()),
                executed);

        InOrder inOrder = Mockito.inOrder(userConnector, authenticationConnector, userCategoryRepository);
        inOrder.verify(userConnector).get(USERNAME);
        inOrder.verify(authenticationConnector).login(userLoginRequest);
        inOrder.verify(userCategoryRepository).findAllByUserId(1L);
    }

    @Test
    public void shouldDeleteUserCorrectly() {
        subject.delete(USERNAME);
        verify(userConnector).delete(USERNAME);
    }

    @Test
    public void shouldGetAllUsersCorrectly() {
        when(userConnector.getAll()).thenReturn(Collections.nCopies(10, user));
        List<UserApi> executed = subject.getAll();
        assertEquals(Collections.nCopies(10, UserApi.newBuilder()
                        .username(USERNAME)
                        .name("First Name")
                        .email(EMAIL)
                        .id(1L)
                        .build()),
                executed);
        verify(userConnector).getAll();
    }

    @Test
    public void shouldChangePasswordCorrectly() throws GenericException {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.newBuilder()
                .newPassword("new")
                .oldPassword("old")
                .build();
        subject.changePassword(USERNAME, "otp", changePasswordRequest);

        InOrder inOrder = Mockito.inOrder(otpService, userConnector);
        inOrder.verify(otpService).validate(USERNAME, "otp");
        inOrder.verify(userConnector).changePassword(USERNAME, changePasswordRequest);
    }

    @Test
    public void shouldUpdateUserCorrectly() throws GenericException {
        user.setPhoto("photoUrl");
        when(cloudStorageService.upload(any())).thenReturn("newUrl");
        UserCategory userCategory = UserCategory.newBuilder()
                .category(Category.newBuilder()
                        .id(2L)
                        .name("categoryName")
                        .build())
                .userId(1L)
                .id(5L)
                .build();
        when(userCategoryRepository.findAllByUserId(any())).thenReturn(Collections.singletonList(userCategory));
        when(categoryRepository.getOne(any())).thenReturn(Category.newBuilder()
                .name("Category")
                .id(10L)
                .build());

        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileName", new byte[10]);
        subject.update(USERNAME, new UpdateUserRequest("Another Name",
                        Collections.singletonList(new CategoryApi(3L, "New Category"))),
                mockMultipartFile);

        InOrder inOrder = Mockito.inOrder(cloudStorageService,
                userConnector,
                userCategoryRepository,
                categoryRepository);
        inOrder.verify(userConnector).get(USERNAME);
        inOrder.verify(cloudStorageService).delete("photoUrl");
        inOrder.verify(cloudStorageService).upload(mockMultipartFile);
        inOrder.verify(userCategoryRepository).findAllByUserId(1L);
        inOrder.verify(userCategoryRepository).deleteInBatch(Collections.singletonList(userCategory));
        inOrder.verify(categoryRepository).getOne(3L);
        inOrder.verify(userCategoryRepository).save(UserCategory.newBuilder()
                .category(Category.newBuilder()
                        .name("Category")
                        .id(10L)
                        .build())
                .userId(1L)
                .build());
        user.setPhoto("newUrl");
        user.setName("Another Name");
        inOrder.verify(userConnector).update(user);
    }

    @Test
    public void shouldNotSaveNorUpdateUserPhotoIfTheseAreNotValid() throws GenericException {
        user.setPhoto("photoUrl");
        subject.update(USERNAME, new UpdateUserRequest(null, Collections.emptyList()), null);
        verifyNoInteractions(cloudStorageService);
        verifyNoInteractions(categoryRepository);
        verify(userConnector).update(user);
    }

}