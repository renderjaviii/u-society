package common.manager.domain.service.user.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import common.manager.app.api.OtpApi;
import common.manager.app.api.TokenApi;
import common.manager.app.api.UserApi;
import common.manager.app.rest.request.CreateUserRequest;
import common.manager.app.rest.request.UserLoginRequest;
import common.manager.domain.converter.Converter;
import common.manager.domain.exception.GenericException;
import common.manager.domain.provider.authentication.AuthenticationConnector;
import common.manager.domain.provider.user.UserConnector;
import common.manager.domain.provider.user.dto.UserDTO;
import common.manager.domain.service.common.CommonServiceImpl;
import common.manager.domain.service.email.MailService;
import common.manager.domain.service.otp.OtpService;
import common.manager.domain.service.user.UserService;

@Service
@EnableConfigurationProperties
public class UserServiceImpl extends CommonServiceImpl implements UserService {

    private static final String USER_ALREADY_EXISTS_FORMAT = "UserName: %s or DocumentNumber: %s or Email: %s or PhoneNumber: %s already registered.";

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
    public TokenApi login(UserLoginRequest request) {
        return Converter.token(authenticationConnector.login(request));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserApi create(CreateUserRequest request) throws GenericException {
        validateUser(request);

        UserDTO userDTO = userConnector.create(request);
        OtpApi userOtp = otpService.create(userDTO.getUsername());
        mailService.sendOtp(request.getEmail(), userOtp.getOtpCode());

        return Converter.user(userDTO);
    }

    private void validateUser(CreateUserRequest request) throws GenericException {
        UserDTO user = userConnector.get(request.getUsername(),
                request.getDocumentNumber(),
                request.getEmail(),
                request.getPhoneNumber());
        if (user != null) {
            String errorMessage = String
                    .format(USER_ALREADY_EXISTS_FORMAT, request.getUsername(), request.getDocumentNumber(),
                            request.getEmail(), request.getPhoneNumber());
            throw new GenericException(errorMessage, "USER_ALREADY_EXISTS");
        }
    }

    @Override
    public UserApi get(String username) {
        return Converter.user(userConnector.getByUsername(username));
    }

    @Override
    public void enableAccount(String username, String otpCode) throws GenericException {
        otpService.validate(username, otpCode);
        userConnector.enableAccount(username);
    }

}
