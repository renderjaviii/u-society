package common.manager.domain.service.otp;

import common.manager.app.api.OtpApi;
import common.manager.domain.exception.GenericException;

public interface OtpService {

    OtpApi create(String username);

    void validate(String username, String otpCode) throws GenericException;

}
