package company.businessmanager.domain.service.user.impl;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import company.businessmanager.app.api.UserApi;
import company.businessmanager.app.rest.request.CreateUserRequest;
import company.businessmanager.app.rest.request.UserLogin;
import company.businessmanager.domain.converter.Converter;
import company.businessmanager.domain.exception.GenericException;
import company.businessmanager.domain.model.Credential;
import company.businessmanager.domain.model.Role;
import company.businessmanager.domain.model.User;
import company.businessmanager.domain.repository.CredentialRepository;
import company.businessmanager.domain.repository.RoleRepository;
import company.businessmanager.domain.repository.UserRepository;
import company.businessmanager.domain.service.user.UserService;

@Service
public class UserServiceImpl implements UserService {

    public static final String WEB_SCOPE = "WEB";
    public static final String GRANT_TYPES = "authorization_code,refresh_token,password,client_credentials";

    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           CredentialRepository credentialRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           Clock clock) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void create(CreateUserRequest request) throws GenericException {
        //  validateUser(request);

        Credential currentCredential = credentialRepository.save(Credential.newBuilder()
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .clientId(request.getDocumentNumber())
                .grantType(GRANT_TYPES)
                .scope(WEB_SCOPE)
                .createdAt(LocalDate.now(clock))
                .clientSecret("secret")
                .build());

        userRepository.save(User.newBuilder()
                .phoneNumber(request.getPhoneNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .lastAccessAt(LocalDateTime.now(clock))
                .createdAt(LocalDate.now(clock))
                .credential(currentCredential)
                .documentNumber(request.getDocumentNumber())
                .roles(buildUserRoles(request))
                .build());
    }

    private void validateUser(CreateUserRequest request) throws GenericException {
        if (credentialRepository.
                findByUsernameOrClientId(request.getUsername(), request.getDocumentNumber()).isPresent()) {
            throw new GenericException("User already exists.", "USER_ALREADY_EXISTS");
        }
    }

    @Override
    public String login(UserLogin request) {
        return null;
    }

    @Override
    public UserApi get(Long userId) throws GenericException {
        try {
            return Converter.converUser(userRepository.getOne(userId));
        } catch (EntityNotFoundException ex) {
            throw new GenericException("User not found.", "USER NOT FOUND");
        }
    }

    private List<Role> buildUserRoles(CreateUserRequest request) throws GenericException {
        List<Role> userRoles = new LinkedList<>();
        for (String userRole : request.getUserRoles()) {
            Role role = roleRepository.findByName(userRole.replaceFirst("", "ROLE_"))
                    .orElseThrow(() ->
                            new GenericException(String.format("The role %s not exists.", userRole), "INVALID_ROLE"));
            userRoles.add(role);
        }
        return userRoles;
    }

}
