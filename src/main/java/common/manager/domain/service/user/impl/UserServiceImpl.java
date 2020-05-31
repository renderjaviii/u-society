package common.manager.domain.service.user.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import common.manager.app.api.OtpApi;
import common.manager.app.api.TokenApi;
import common.manager.app.api.UserApi;
import common.manager.app.rest.request.CreateUserRequest;
import common.manager.app.rest.request.UserLoginRequest;
import common.manager.app.rest.response.LoginResponse;
import common.manager.domain.exception.GenericException;
import common.manager.domain.exception.WebException;
import common.manager.domain.provider.authentication.AuthenticationConnector;
import common.manager.domain.provider.user.UserConnector;
import common.manager.domain.service.common.CommonServiceImpl;
import common.manager.domain.service.email.MailService;
import common.manager.domain.service.otp.OtpService;
import common.manager.domain.service.user.UserService;
import common.manager.domain.util.mapper.Constant;

@Service
@EnableConfigurationProperties
public class UserServiceImpl extends CommonServiceImpl implements UserService {

    private final AuthenticationConnector authenticationConnector;
    private final UserConnector userConnector;
    private final MailService mailService;
    private final OtpService otpService;

    @Autowired
    public UserServiceImpl(AuthenticationConnector authenticationConnector,
                           UserConnector userConnector,
                           MailService mailService,
                           OtpService otpService) {
        this.authenticationConnector = authenticationConnector;
        this.userConnector = userConnector;
        this.mailService = mailService;
        this.otpService = otpService;
    }

    @Override
    public UserApi create(CreateUserRequest request) throws GenericException {
        validateUser(request);

        UserApi user = userConnector.create(request);
        OtpApi userOtp = otpService.create(user.getUsername());
        mailService.sendOtp(request.getEmail(), userOtp.getOtpCode());
        return user;
    }

    @Override
    public UserApi get(String username) {
        return userConnector.get(username);
    }

    @Override
    public void enableAccount(String username, String otpCode) throws GenericException {
        otpService.validate(username, otpCode);
        userConnector.enableAccount(username);
    }

    @Override
    public LoginResponse login(UserLoginRequest request) {
        UserApi user = userConnector.get(request.getUsername());
        TokenApi token = authenticationConnector.login(request);
        return new LoginResponse(user, token);
    }

    @Override
    public void delete(String username) {
        userConnector.delete(username);

    }

    @Override
    public List<UserApi> getAll() {
        return userConnector.getAll();
    }

    private void validateUser(CreateUserRequest request) throws GenericException {
        try {
            UserApi user = userConnector.get(request.getUsername(), request.getDocumentNumber(),
                    request.getEmail(), request.getPhoneNumber());
            if (user != null) {
                throw new GenericException("User already exists.", "USER_ALREADY_EXISTS");
            }
        } catch (WebException ex) {
            if (!Constant.USER_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new GenericException(ex.getMessage());
            }
        }
    }

}
