package common.manager.domain.service.user.impl;

import java.time.Clock;
import java.time.LocalDate;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import common.manager.app.api.UserApi;
import common.manager.app.rest.request.CreateUserRequest;
import common.manager.domain.converter.Converter;
import common.manager.domain.exception.GenericException;
import common.manager.domain.model.Role;
import common.manager.domain.model.User;
import common.manager.domain.repository.RoleRepository;
import common.manager.domain.repository.UserRepository;
import common.manager.domain.service.common.CommonServiceImpl;
import common.manager.domain.service.user.UserService;

@Service
@EnableConfigurationProperties
public class UserServiceImpl extends CommonServiceImpl implements UserService {

    private static final String ROLE_ERROR_FORMAT = "The role: %s not exists.";
    private static final String USER_NOT_FOUND_FORMAT = "Username: %s not found.";
    public static final String USER_ALREADY_EXISTS_FORMAT = "UserName: %s or DocumentNumber: %s  already registered.";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           Clock clock) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void create(CreateUserRequest request) throws GenericException {
        validateUser(request);

        userRepository.save(User.newBuilder()
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .createdAt(LocalDate.now(clock))
                .documentNumber(request.getDocumentNumber())
                .role(buildUserRoles(request))
                .build());
    }

    private void validateUser(CreateUserRequest request) throws GenericException {
        if (userRepository.findByUsernameAndDocumentNumber(request.getUsername(), request.getDocumentNumber())
                .isPresent()) {
            throw new GenericException(String.format(USER_ALREADY_EXISTS_FORMAT,
                    request.getUsername(), request.getDocumentNumber()), "USER_ALREADY_EXISTS");
        }
    }

    @Override
    public Authentication getTokenInfo() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public UserApi get(String username) throws GenericException {
        return userRepository.findByUsername(username)
                .map(Converter::user)
                .orElseThrow(
                        () -> new GenericException(String.format(USER_NOT_FOUND_FORMAT, username), "USER NOT FOUND"));
    }

    private Role buildUserRoles(CreateUserRequest request) throws GenericException {
        return roleRepository.findByName(request.getUserRole().replaceFirst("", "ROLE_"))
                .orElseThrow(() -> new GenericException(String.format(ROLE_ERROR_FORMAT, request.getUserRole()),
                        "INVALID_ROLE"));
    }

}
