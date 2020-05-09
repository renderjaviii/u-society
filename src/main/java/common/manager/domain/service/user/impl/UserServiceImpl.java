package common.manager.domain.service.user.impl;

import static java.lang.Boolean.TRUE;

import java.time.Clock;
import java.time.LocalDate;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import common.manager.app.api.OtpApi;
import common.manager.app.api.UserApi;
import common.manager.app.rest.request.CreateUserRequest;
import common.manager.app.rest.response.CreateUserResponse;
import common.manager.domain.converter.Converter;
import common.manager.domain.exception.GenericException;
import common.manager.domain.model.Role;
import common.manager.domain.model.User;
import common.manager.domain.repository.RoleRepository;
import common.manager.domain.repository.UserRepository;
import common.manager.domain.service.common.CommonServiceImpl;
import common.manager.domain.service.email.MailService;
import common.manager.domain.service.oauth.PasswordManager;
import common.manager.domain.service.otp.OtpService;
import common.manager.domain.service.user.UserService;

@Service
@EnableConfigurationProperties
public class UserServiceImpl extends CommonServiceImpl implements UserService {

    private static final String ROLE_ERROR_FORMAT = "The role: %s not exists.";
    private static final String USER_ALREADY_EXISTS_FORMAT = "UserName: %s or DocumentNumber: %s already registered.";
    private static final String EMAIL_ALREADY_IN_USE = "Email: %s is already in use.";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordManager passwordManager;
    private final OtpService otpService;
    private final MailService mailService;
    private final Clock clock;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordManager passwordManager,
                           OtpService otpService,
                           MailService mailService,
                           Clock clock) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordManager = passwordManager;
        this.otpService = otpService;
        this.mailService = mailService;
        this.clock = clock;
    }

    @Override
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
                .orElseThrow(() -> new GenericException(String.format(ROLE_ERROR_FORMAT, request.getUserRole()), "INVALID_ROLE"));
    }

}
