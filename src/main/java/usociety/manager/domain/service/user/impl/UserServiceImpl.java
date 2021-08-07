package usociety.manager.domain.service.user.impl;

import static usociety.manager.domain.util.Constants.USER_NOT_FOUND;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.OtpApi;
import usociety.manager.app.api.TokenApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.app.rest.request.LoginRequest;
import usociety.manager.app.rest.request.UpdateUserRequest;
import usociety.manager.app.rest.response.LoginResponse;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.exception.WebException;
import usociety.manager.domain.provider.authentication.AuthenticationConnector;
import usociety.manager.domain.provider.user.UserConnector;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.repository.UserCategoryRepository;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.otp.OtpService;
import usociety.manager.domain.service.user.CreateUserDelegate;
import usociety.manager.domain.service.user.UpdateUserDelegate;
import usociety.manager.domain.service.user.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final AuthenticationConnector authenticationConnector;
    private final UserCategoryRepository userCategoryRepository;
    private final UpdateUserDelegate updateUserDelegate;
    private final CreateUserDelegate createUserDelegate;
    private final UserConnector userConnector;
    private final MailService mailService;
    private final OtpService otpService;

    @Autowired
    public UserServiceImpl(AuthenticationConnector authenticationConnector,
                           UserCategoryRepository userCategoryRepository,
                           UpdateUserDelegate updateUserDelegate,
                           CreateUserDelegate createUserDelegate,
                           UserConnector userConnector,
                           MailService mailService,
                           OtpService otpService) {
        this.authenticationConnector = authenticationConnector;
        this.userCategoryRepository = userCategoryRepository;
        this.updateUserDelegate = updateUserDelegate;
        this.createUserDelegate = createUserDelegate;
        this.userConnector = userConnector;
        this.mailService = mailService;
        this.otpService = otpService;
    }

    @Override
    public LoginResponse create(CreateUserRequest request) throws GenericException {
        validateUser(request.getUsername(), request.getEmail());
        UserApi user = createUserDelegate.execute(request);

        TokenApi token = authenticationConnector.login(new LoginRequest(request.getUsername(), request.getPassword()));
        return new LoginResponse(user, token);
    }

    @Override
    public void verify(String email) throws GenericException {
        validateUser(null, email);
        OtpApi userOtp = otpService.create(email);
        mailService.sendOtp(email, userOtp.getOtpCode());
    }

    @Override
    public UserApi get(String username) {
        UserApi user = Converter.user(userConnector.get(username));
        setUserCategories(user);
        return user;
    }

    @Override
    public UserApi getById(Long id) {
        return Converter.user(userConnector.get(id, null, null));
    }

    @Override
    public void enableAccount(String email, String otpCode) throws GenericException {
        otpService.validate(email, otpCode);
        UserDTO user = userConnector.get(null, null, email);
        userConnector.enableAccount(user.getUsername());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        UserDTO user = userConnector.get(request.getUsername());
        TokenApi token = authenticationConnector.login(request);

        UserApi userApi = Converter.user(user);
        setUserCategories(userApi);
        return new LoginResponse(userApi, token);
    }

    @Override
    public void delete(String username) {
        //Any more data is removed due to the user is not actually deleted (only disabled).
        userConnector.delete(username);
    }

    @Override
    public List<UserApi> getAll() {
        return userConnector.getAll()
                .stream()
                .map(Converter::user)
                .collect(Collectors.toList());
    }

    @Override
    public void changePassword(String username, String otpCode, ChangePasswordRequest request) throws GenericException {
        otpService.validate(username, otpCode);
        userConnector.changePassword(username, request);
    }

    @Override
    public UserApi update(String username, UpdateUserRequest request) throws GenericException {
        UserApi user = updateUserDelegate.execute(username, request);
        setUserCategories(user);
        return user;
    }

    private void validateUser(String username, String email) throws GenericException {
        try {
            UserDTO user = userConnector.get(null, username, email);
            if (Objects.nonNull(user)) {
                throw new GenericException("User is already registered", "USER_ALREADY_EXISTS");
            }
        } catch (WebException ex) {
            if (!USER_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new GenericException(ex.getMessage());
            }
        }
    }

    private void setUserCategories(UserApi userApi) {
        userApi.setCategoryList(userCategoryRepository.findAllByUserId(userApi.getId())
                .stream()
                .map(userCategory -> Converter.category(userCategory.getCategory()))
                .collect(Collectors.toList()));
    }

}
