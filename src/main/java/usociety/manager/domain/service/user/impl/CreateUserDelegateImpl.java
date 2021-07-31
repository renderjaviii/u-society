package usociety.manager.domain.service.user.impl;

import static java.lang.Boolean.TRUE;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.provider.user.UserConnector;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.otp.OtpService;
import usociety.manager.domain.service.user.CreateUserDelegate;

@Component
public class CreateUserDelegateImpl implements CreateUserDelegate {

    private static final String EMAIL_CONTENT = "<html><body>" +
            "<h3>¡Hola <u>%s</u>!</h3>" +
            "<p>Bienvenido a <a href='https://usociety-68208.web.app/'>U Society</a>, logueate y descrubre todo lo que tenemos para ti.</p>" +
            "</html></body>";

    @Value("${config.user.validate-otp:0}")
    private boolean validateOtp;

    private final CloudStorageService cloudStorageService;
    private final UserConnector userConnector;
    private final MailService mailService;
    private final OtpService otpService;

    @Autowired
    public CreateUserDelegateImpl(CloudStorageService cloudStorageService,
                                  UserConnector userConnector,
                                  MailService mailService,
                                  OtpService otpService) {
        this.cloudStorageService = cloudStorageService;
        this.userConnector = userConnector;
        this.mailService = mailService;
        this.otpService = otpService;
    }

    @Override
    public UserApi execute(CreateUserRequest request) throws GenericException {
        if (validateOtp) {
            otpService.validate(request.getEmail(), request.getOtpCode());
        }

        String photoUrl = cloudStorageService.upload(request.getPhoto());
        request.setPhoto(photoUrl);

        UserDTO userDTO;
        try {
            userDTO = userConnector.create(request);
        } catch (Exception ex) {
            cloudStorageService.delete(photoUrl);
            throw new GenericException("User could not be created", "ERROR_CREATING_USER", ex);
        }

        mailService.send(request.getEmail(), buildEmailContent(request), TRUE);

        return Converter.user(userDTO);
    }

    private String buildEmailContent(CreateUserRequest request) {
        return String.format(EMAIL_CONTENT, StringUtils.capitalize(request.getName()));
    }

}
