package usociety.manager.domain.service.user.impl;

import static java.lang.Boolean.TRUE;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import usociety.manager.app.api.OtpApi;
import usociety.manager.app.api.TokenApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.app.rest.request.UpdateUserRequest;
import usociety.manager.app.rest.request.UserLoginRequest;
import usociety.manager.app.rest.response.LoginResponse;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.exception.WebException;
import usociety.manager.domain.model.UserCategory;
import usociety.manager.domain.provider.authentication.AuthenticationConnector;
import usociety.manager.domain.provider.user.UserConnector;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.repository.CategoryRepository;
import usociety.manager.domain.repository.UserCategoryRepository;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.otp.OtpService;
import usociety.manager.domain.service.user.UserService;
import usociety.manager.domain.util.Constants;

@Service
public class UserServiceImpl implements UserService {

    private static final String EMAIL_CONTENT = "<html><body>" +
            "<h3>¡Hola <u>%s</u>!</h3>" +
            "<p>Bienvenido a <a href='https://usociety-68208.web.app/'>U Society</a>, logueate y descrubre todo lo que tenemos para ti.</p>" +
            "</html></body>";

    private final AuthenticationConnector authenticationConnector;
    private final UserCategoryRepository userCategoryRepository;
    private final CloudStorageService cloudStorageService;
    private final CategoryRepository categoryRepository;
    private final UserConnector userConnector;
    private final MailService mailService;
    private final OtpService otpService;

    @Value("${config.user.validate-otp:0}")
    private boolean validateOtp;

    @Autowired
    public UserServiceImpl(AuthenticationConnector authenticationConnector,
                           UserCategoryRepository userCategoryRepository,
                           CloudStorageService cloudStorageService,
                           CategoryRepository categoryRepository,
                           UserConnector userConnector,
                           MailService mailService,
                           OtpService otpService) {
        this.authenticationConnector = authenticationConnector;
        this.userCategoryRepository = userCategoryRepository;
        this.cloudStorageService = cloudStorageService;
        this.categoryRepository = categoryRepository;
        this.userConnector = userConnector;
        this.mailService = mailService;
        this.otpService = otpService;
    }

    @Override
    public LoginResponse create(CreateUserRequest request) throws GenericException {
        validateUser(request.getUsername(), request.getEmail());

        if (validateOtp) {
            otpService.validate(request.getEmail(), request.getOtpCode());
        }

        String photoUrl = cloudStorageService.upload(request.getPhoto());
        request.setPhoto(photoUrl);

        try {
            userConnector.create(request);
        } catch (Exception ex) {
            cloudStorageService.delete(photoUrl);
            throw new GenericException("User does not exist", "ERROR_CREATING_USER", ex);
        }

        mailService.send(request.getEmail(), buildEmailContent(request), TRUE);
        return login(UserLoginRequest.newBuilder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build());
    }

    @Override
    public void verify(String email, boolean resendCode) throws GenericException {
        if (!resendCode) {
            validateUser(null, email);
        }
        OtpApi userOtp = otpService.create(email);
        mailService.sendOtp(email, userOtp.getOtpCode());
    }

    @Override
    public UserApi get(String username) {
        UserApi user = Converter.user(userConnector.get(username));
        user.setCategoryList(userCategoryRepository.findAllByUserId(user.getId())
                .stream()
                .map(userCategory -> Converter.category(userCategory.getCategory()))
                .collect(Collectors.toList()));

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
    public LoginResponse login(UserLoginRequest request) {
        UserDTO user = userConnector.get(request.getUsername());
        TokenApi token = authenticationConnector.login(request);

        UserApi userApi = Converter.user(user);
        userApi.setCategoryList(userCategoryRepository.findAllByUserId(userApi.getId())
                .stream()
                .map(userCategory -> Converter.category(userCategory.getCategory()))
                .collect(Collectors.toList()));

        return new LoginResponse(userApi, token);
    }

    @Override
    public void delete(String username) {
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
    public void update(String username, UpdateUserRequest request) throws GenericException {
        UserApi user = get(username);
        user.setName(StringUtils.defaultString(request.getName(), user.getName()));
        user.setPhoto(processPhotoAndGetUrl(user, request));

        List<UserCategory> userCategoryList = userCategoryRepository.findAllByUserId(user.getId());
        userCategoryRepository.deleteInBatch(userCategoryList);

        if (!CollectionUtils.isEmpty(request.getCategoryList())) {
            request.getCategoryList()
                    .forEach(categoryApi -> userCategoryRepository.save(UserCategory.newBuilder()
                            .category(categoryRepository.getOne(categoryApi.getId()))
                            .userId(user.getId())
                            .build())
                    );
        }

        userConnector.update(Converter.user(user));
    }

    private void validateUser(String username, String email) throws GenericException {
        try {
            UserDTO user = userConnector.get(null, username, email);
            if (Objects.nonNull(user)) {
                throw new GenericException("User already registered", "USER_ALREADY_EXISTS");
            }
        } catch (WebException ex) {
            if (!Constants.USER_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new GenericException(ex.getMessage());
            }
        }
    }

    private String buildEmailContent(CreateUserRequest request) {
        return String.format(EMAIL_CONTENT, StringUtils.capitalize(request.getName()));
    }

    private String processPhotoAndGetUrl(UserApi user, UpdateUserRequest request)
            throws GenericException {
        String currentUserPhoto = user.getPhoto();
        if (Objects.nonNull(request.getPhoto()) && !request.getPhoto().equals(user.getPhoto())) {
            if (StringUtils.isNotEmpty(currentUserPhoto)) {
                cloudStorageService.delete(currentUserPhoto);
            }
            return cloudStorageService.upload(request.getPhoto());
        }
        return currentUserPhoto;
    }

}
