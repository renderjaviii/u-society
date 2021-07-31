package usociety.manager.domain.service.user.impl;

import static org.mockito.ArgumentMatchers.any;
import static usociety.manager.app.api.UserApiFixture.email;
import static usociety.manager.app.api.UserApiFixture.username;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import usociety.manager.app.api.OtpApi;
import usociety.manager.app.api.TokenApiFixture;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserApiFixture;
import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.app.rest.request.LoginRequest;
import usociety.manager.app.rest.request.UpdateUserRequest;
import usociety.manager.app.rest.response.LoginResponse;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.exception.WebException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.UserCategory;
import usociety.manager.domain.provider.authentication.AuthenticationConnector;
import usociety.manager.domain.provider.user.UserConnector;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.repository.UserCategoryRepository;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.otp.OtpService;
import usociety.manager.domain.service.user.CreateUserDelegate;
import usociety.manager.domain.service.user.UpdateUserDelegate;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final String OTP_CODE = "2468";

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
    @InjectMocks
    private UserServiceImpl subject;

    UserDTO userDTO;
    UserApi userApi;

    @Before
    public void setUp() {
        userDTO = UserDTO.newBuilder()
                .username(username)
                .email(email)
                .name(UserApiFixture.name)
                .id(UserApiFixture.id)
                .build();

        userApi = UserApi.newBuilder()
                .username(username)
                .email(email)
                .name(UserApiFixture.name)
                .id(UserApiFixture.id)
                .build();

        Mockito.when(userConnector.get(any(), any(), any())).thenReturn(userDTO);
        Mockito.when(userConnector.get(any())).thenReturn(userDTO);
    }

    @Test
    public void shouldCreateUserCorrectly() throws GenericException {
        Mockito.reset(userConnector);
        Mockito.when(authenticationConnector.login(any())).thenReturn(TokenApiFixture.defaultValue);
        Mockito.when(createUserDelegate.execute(any())).thenReturn(UserApiFixture.defaultValue);

        CreateUserRequest request = CreateUserRequest.newBuilder()
                .password(UserApiFixture.password)
                .username(username)
                .name("John Doe")
                .otpCode("2468")
                .email(email)
                .build();

        LoginResponse executed = subject.create(request);

        Assert.assertEquals(new LoginResponse(UserApiFixture.defaultValue, TokenApiFixture.defaultValue), executed);

        InOrder inOrder = Mockito.inOrder(userConnector, authenticationConnector, createUserDelegate);
        inOrder.verify(userConnector).get(null, username, email);
        inOrder.verify(createUserDelegate).execute(request);
        inOrder.verify(authenticationConnector)
                .login(new LoginRequest(username, UserApiFixture.password));
    }

    @Test
    public void shouldVerifyEmailAndSendOTPCorrectly() throws GenericException {
        Mockito.reset(userConnector);
        Mockito.when(otpService.create(any())).thenReturn(OtpApi.newBuilder()
                .otpCode(OTP_CODE)
                .build());

        subject.verify(email);

        InOrder inOrder = Mockito.inOrder(userConnector, otpService, mailService);
        inOrder.verify(userConnector).get(null, null, email);
        inOrder.verify(otpService).create(email);
        inOrder.verify(mailService).sendOtp(email, OTP_CODE);
    }

    @Test
    public void shouldGetUserByUsernameCorrectly() {
        userApi.setCategoryList(Collections.emptyList());

        UserApi executed = subject.get(username);

        Assert.assertEquals(userApi, executed);
        Mockito.verify(userConnector, Mockito.only()).get(username);
    }

    @Test
    public void shouldGetUserByIdCorrectly() {
        UserApi executed = subject.getById(1L);

        Assert.assertEquals(userApi, executed);
        Mockito.verify(userConnector, Mockito.only()).get(1L, null, null);
    }

    @Test
    public void shouldEnableUserCorrectly() throws GenericException {
        subject.enableAccount(username, OTP_CODE);

        Mockito.verify(otpService).validate(username, OTP_CODE);
        Mockito.verify(userConnector).enableAccount(username);
    }

    @Test
    public void shouldMakeUserLoginCorrectly() {
        userApi.setCategoryList(UserApiFixture.categoryList);

        Mockito.when(authenticationConnector.login(any())).thenReturn(TokenApiFixture.defaultValue);

        Category category = new Category(UserApiFixture.category.getId(), UserApiFixture.category.getName());
        Mockito.when(userCategoryRepository.findAllByUserId(any()))
                .thenReturn(Collections.singletonList(new UserCategory(20L, UserApiFixture.id, category)));

        LoginRequest loginRequest = new LoginRequest(username, UserApiFixture.password);
        LoginResponse executed = subject.login(loginRequest);

        Assert.assertEquals(new LoginResponse(userApi, TokenApiFixture.defaultValue), executed);

        InOrder inOrder = Mockito.inOrder(userConnector, authenticationConnector, userCategoryRepository);
        inOrder.verify(userConnector).get(username);
        inOrder.verify(authenticationConnector).login(loginRequest);
        inOrder.verify(userCategoryRepository).findAllByUserId(UserApiFixture.id);
    }

    @Test
    public void shouldDeleteUserCorrectly() {
        subject.delete(username);
        Mockito.verify(userConnector).delete(username);
    }

    @Test
    public void shouldGetAllUsersCorrectly() {
        Mockito.when(userConnector.getAll()).thenReturn(Collections.nCopies(11, userDTO));

        List<UserApi> executed = subject.getAll();

        Assert.assertEquals(Collections.nCopies(11, userApi), executed);
        Mockito.verify(userConnector).getAll();
    }

    @Test
    public void shouldChangeUserPasswordCorrectly() throws GenericException {
        ChangePasswordRequest request = new ChangePasswordRequest("Pass123", "Qwerty987");

        subject.changePassword(username, OTP_CODE, request);

        InOrder inOrder = Mockito.inOrder(otpService, userConnector);
        inOrder.verify(otpService).validate(username, OTP_CODE);
        inOrder.verify(userConnector).changePassword(username, request);
    }

    @Test
    public void shouldUpdateUserCorrectly() throws GenericException {
        Mockito.when(updateUserDelegate.execute(any(), any())).thenReturn(UserApiFixture.defaultValue);

        UpdateUserRequest request = new UpdateUserRequest("Another Name", null, Collections.singleton(3L));
        UserApi executed = subject.update(username, request);

        Assert.assertEquals(UserApiFixture.defaultValue, executed);
        Mockito.verify(updateUserDelegate, Mockito.only()).execute(username, request);
    }

    @Test(expected = GenericException.class)
    public void shouldFailCreatingUserIfAlreadyExists() throws GenericException {
        try {
            subject.create(new CreateUserRequest());
        } catch (GenericException e) {
            Assert.assertEquals("USER_ALREADY_EXISTS", e.getErrorCode());
            Mockito.verifyNoInteractions(createUserDelegate, authenticationConnector);
            throw e;
        }
        Assert.fail();
    }

    @Test(expected = GenericException.class)
    public void shouldFailCreatingUserIfCommunicationFails() throws GenericException {
        String errorMessage = "Error message";
        Mockito.when(userConnector.get(any(), any(), any()))
                .thenThrow(new WebException(errorMessage, "COMMUNICATION_FAILURE"));

        try {
            subject.create(new CreateUserRequest());
        } catch (GenericException e) {
            Assert.assertEquals("UNEXPECTED_ERROR", e.getErrorCode());
            Assert.assertEquals(errorMessage, e.getMessage());
            Mockito.verifyNoInteractions(createUserDelegate, authenticationConnector);
            throw e;
        }
        Assert.fail();
    }

    @Test(expected = GenericException.class)
    public void shouldFailVerifyingEmailIfCommunicationFails() throws GenericException {
        Mockito.when(userConnector.get(any(), any(), any()))
                .thenThrow(new WebException("Error message", "COMMUNICATION_FAILURE"));
        try {
            subject.verify(email);
        } catch (GenericException e) {
            Assert.assertEquals("Error message", e.getMessage());
            Mockito.verifyNoInteractions(otpService, mailService);
            throw e;
        }
        Assert.fail();
    }

}