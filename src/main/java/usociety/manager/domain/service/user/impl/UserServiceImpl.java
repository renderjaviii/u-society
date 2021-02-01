package usociety.manager.domain.service.user.impl;

import static java.lang.Boolean.TRUE;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
import usociety.manager.domain.service.aws.s3.CloudStorageService;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.otp.OtpService;
import usociety.manager.domain.service.user.UserService;
import usociety.manager.domain.util.Constant;

@Service
public class UserServiceImpl implements UserService {

    private final AuthenticationConnector authenticationConnector;
    private final UserCategoryRepository userCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UserConnector userConnector;
    private final MailService mailService;
    private final OtpService otpService;
    private final CloudStorageService cloudStorageService;

    @Value("${config.user.validate-otp:0}")
    private boolean validateOtp;

    @Autowired
    public UserServiceImpl(AuthenticationConnector authenticationConnector,
                           UserConnector userConnector,
                           MailService mailService,
                           OtpService otpService,
                           CloudStorageService cloudStorageService,
                           UserCategoryRepository userCategoryRepository,
                           CategoryRepository categoryRepository) {
        this.authenticationConnector = authenticationConnector;
        this.userConnector = userConnector;
        this.mailService = mailService;
        this.otpService = otpService;
        this.cloudStorageService = cloudStorageService;
        this.userCategoryRepository = userCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public LoginResponse create(CreateUserRequest request, MultipartFile photo) throws GenericException {
        validateUser(request.getUsername(), request.getEmail());

        if (validateOtp) {
            otpService.validate(request.getEmail(), request.getOtpCode());
        }

        String photoUrl = cloudStorageService.upload(photo);
        request.setPhoto(photoUrl);

        try {
            userConnector.create(request);
        } catch (Exception ex) {
            cloudStorageService.delete(photoUrl);
            throw new GenericException("El usuario no pudo ser creado", "USER_NOT_CREATED_ERROR");
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
        return Converter.user(userConnector.get(username));
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
    public void update(String username, UpdateUserRequest request, MultipartFile photo) throws GenericException {
        UserApi user = get(username);
        String photoUrl = null;
        String currentUserPhoto = user.getPhoto();
        if (Objects.nonNull(photo) && !photo.isEmpty()) {
            if (StringUtils.isNotEmpty(currentUserPhoto)) {
                cloudStorageService.delete(currentUserPhoto);
            }
            photoUrl = cloudStorageService.upload(photo);
        }

        user.setName(StringUtils.defaultString(request.getName(), user.getName()));
        user.setPhoto(StringUtils.defaultString(photoUrl, currentUserPhoto));

        List<UserCategory> userCategoryList = userCategoryRepository.findAllByUserId(user.getId());
        userCategoryRepository.deleteInBatch(userCategoryList);

        request.getCategoryList()
                .forEach(categoryApi -> userCategoryRepository.save(UserCategory.newBuilder()
                        .category(categoryRepository.getOne(categoryApi.getId()))
                        .userId(user.getId())
                        .build()));

        userConnector.update(Converter.user(user));
    }

    private void validateUser(String username, String email) throws GenericException {
        try {
            UserDTO user = userConnector.get(null, username, email);
            if (user != null) {
                throw new GenericException("Usuario ya registrado, por favor verifica la información.",
                        "USER_ALREADY_EXISTS");
            }
        } catch (WebException ex) {
            if (!Constant.USER_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new GenericException(ex.getMessage());
            }
        }
    }

    private String buildEmailContent(CreateUserRequest request) {
        return String.format("<html><body>" +
                        "<h3>¡Hola <u>%s</u>!</h3>" +
                        "<p>Bienvenido a <b>U Society</b>, logueate y descrubre todo lo que tenemos para ti.</p>" +
                        "</html></body>",
                StringUtils.capitalize(request.getName()));
    }

}
