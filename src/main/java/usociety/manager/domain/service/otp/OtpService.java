package usociety.manager.domain.service.otp;

import usociety.manager.app.api.OtpApi;
import usociety.manager.domain.exception.GenericException;

public interface OtpService {

    OtpApi create(String email);

    void validate(String email, String otpCode) throws GenericException;

}
