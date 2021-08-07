package usociety.manager.domain.service.user.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static usociety.manager.app.api.UserApiFixture.email;
import static usociety.manager.app.api.UserApiFixture.username;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserApiFixture;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.exception.WebException;
import usociety.manager.domain.provider.user.UserConnector;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.otp.OtpService;

@RunWith(MockitoJUnitRunner.class)
public class CreateUserDelegateImplTest {

    private static final String OTP_CODE = "987654";

    @Mock
    private CloudStorageService cloudStorageService;
    @Mock
    private UserConnector userConnector;
    @Mock
    private MailService mailService;
    @Mock
    private OtpService otpService;

    @InjectMocks
    private CreateUserDelegateImpl subject;

    private CreateUserRequest request;
    private UserDTO userDTO;
    private UserApi userApi;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(subject, "validateOtp", TRUE, Boolean.class);

        request = CreateUserRequest.newBuilder()
                .password(UserApiFixture.password)
                .photo("base64Image")
                .username(username)
                .otpCode(OTP_CODE)
                .name("John Doe")
                .email(email)
                .build();

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
    }

    @Test
    public void shouldCreateUserCorrectly() throws GenericException {
        String photoUrl = "photo-url";
        when(cloudStorageService.uploadImage(any())).thenReturn(photoUrl);
        when(userConnector.create(any())).thenReturn(userDTO);

        UserApi executed = subject.execute(request);

        Assert.assertEquals(userApi, executed);

        InOrder inOrder = Mockito.inOrder(otpService, cloudStorageService, userConnector);
        inOrder.verify(otpService).validate(email, OTP_CODE);
        inOrder.verify(cloudStorageService).uploadImage("base64Image");
        inOrder.verify(userConnector).create(request);
    }

    @Test
    public void shouldCreateUserWithNoOTPValidationCorrectly() throws GenericException {
        ReflectionTestUtils.setField(subject, "validateOtp", FALSE);

        String photoUrl = "photo-url";
        when(cloudStorageService.uploadImage(any())).thenReturn(photoUrl);
        when(userConnector.create(any())).thenReturn(userDTO);

        UserApi executed = subject.execute(request);

        Assert.assertEquals(userApi, executed);

        InOrder inOrder = Mockito.inOrder(otpService, cloudStorageService, userConnector);
        inOrder.verify(cloudStorageService).uploadImage("base64Image");
        inOrder.verify(userConnector).create(request);

        Mockito.verifyNoInteractions(otpService);

    }

    @Test(expected = GenericException.class)
    public void shouldNotCreateUserIfOtpCodeIsInvalid() throws GenericException {
        Mockito.doThrow(new GenericException("Invalid OTP", "INVALID_OTP"))
                .when(otpService).validate(any(), any());

        try {
            subject.execute(CreateUserRequest.newBuilder().username(username).otpCode(OTP_CODE).build());
        } catch (GenericException e) {
            assertEquals("INVALID_OTP", e.getErrorCode());
            verifyNoInteractions(cloudStorageService);
            verifyNoInteractions(mailService);
            throw e;
        }
        fail();
    }

    @Test(expected = GenericException.class)
    public void shouldNotCreateUserIfImageCannotBeUploaded() throws GenericException {
        String errorMessage = "Image could not be uploaded";
        when(cloudStorageService.uploadImage(any())).thenThrow(new GenericException(errorMessage));
        try {
            subject.execute(CreateUserRequest.newBuilder().username(username).otpCode(OTP_CODE).build());
        } catch (GenericException e) {
            assertEquals(errorMessage, e.getMessage());
            verifyNoInteractions(userConnector);
            verifyNoInteractions(mailService);
            throw e;
        }
        fail();
    }

    @Test(expected = GenericException.class)
    public void shouldDeleteImageIfUserCreationFails() throws GenericException {
        String photoUrl = "photo-url";
        when(cloudStorageService.uploadImage(any())).thenReturn(photoUrl);
        when(userConnector.create(any())).thenThrow(new WebException("Error."));

        try {
            subject.execute(request);
        } catch (GenericException e) {
            assertEquals("ERROR_CREATING_USER", e.getErrorCode());
            verify(cloudStorageService).delete(photoUrl);
            throw e;
        }
        fail();
    }

}