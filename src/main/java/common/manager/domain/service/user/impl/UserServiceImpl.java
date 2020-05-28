package common.manager.domain.service.user.impl;

import java.time.Clock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import common.manager.app.api.TokenApi;
import common.manager.app.rest.request.UserLoginRequest;
import common.manager.domain.converter.Converter;
import common.manager.domain.provider.authentication.AuthenticationConnector;
import common.manager.domain.service.common.CommonServiceImpl;
import common.manager.domain.service.email.MailService;
import common.manager.domain.service.otp.OtpService;
import common.manager.domain.service.user.UserService;

@Service
@EnableConfigurationProperties
public class UserServiceImpl extends CommonServiceImpl implements UserService {

    private static final String ROLE_ERROR_FORMAT = "The role: %s not exists.";
    private static final String USER_ALREADY_EXISTS_FORMAT = "UserName: %s or DocumentNumber: %s already registered.";
    private static final String EMAIL_ALREADY_IN_USE = "Email: %s is already in use.";

    private final AuthenticationConnector authenticationConnector;
    private final OtpService otpService;
    private final MailService mailService;
    private final Clock clock;

    @Autowired
    public UserServiceImpl(OtpService otpService,
                           MailService mailService,
                           Clock clock,
                           AuthenticationConnector authenticationConnector) {
        this.otpService = otpService;
        this.mailService = mailService;
        this.clock = clock;
        this.authenticationConnector = authenticationConnector;
    }

    @Override
    public TokenApi login(UserLoginRequest request) {
        return Converter.token(authenticationConnector.login(request));
    }

   /* @Override
    @Transactional(rollbackOn = Exception.class)
    public CreateUserResponse create(CreateUserRequest request) throws GenericException {
        validateUser(request);

        userRepository.save(User.newBuilder()
                .password(passwordManager.encode(request.getPassword()))
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .createdAt(LocalDate.now(clock))
                .documentNumber(request.getDocumentNumber())
                .email(request.getEmail())
                .role(buildUserRoles(request))
                .build());

        OtpApi userOtp = otpService.create(request.getUsername());
        mailService.sendOtp(request.getEmail(), userOtp.getOtpCode());

        return new CreateUserResponse(userOtp.getExpiresAt());
    }



    private void validateUser(CreateUserRequest request) throws GenericException {
        if (userRepository.findByUsernameOrDocumentNumber(request.getUsername(), request.getDocumentNumber())
                .isPresent()) {
            throw new GenericException(String.format(USER_ALREADY_EXISTS_FORMAT,
                    request.getUsername(), request.getDocumentNumber()), "USER_ALREADY_EXISTS");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new GenericException(String.format(EMAIL_ALREADY_IN_USE, request.getEmail()), "EMAIL_ALREADY_IN_USE");
        }
    }

    @Override
    public Authentication getTokenInfo() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public UserApi get(String username) throws GenericException {
        return Converter.user(getUser(username));
    }

    @Override
    public void enableAccount(String username, String otpCode) throws GenericException {
        otpService.validate(username, otpCode);

        User user = getUser(username);
        user.setEmailVerified(TRUE);
        userRepository.save(user);
    }

    private Role buildUserRoles(CreateUserRequest request) throws GenericException {
        return roleRepository.findByName(request.getUserRole().replaceFirst("", "ROLE_"))
                .orElseThrow(() -> new GenericException(String.format(ROLE_ERROR_FORMAT, request.getUserRole()),
                        "INVALID_ROLE"));
    }*/

}
