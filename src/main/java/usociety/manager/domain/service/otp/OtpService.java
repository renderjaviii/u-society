package usociety.manager.domain.service.otp;

import usociety.manager.app.api.OtpApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.common.CommonService;

public interface OtpService extends CommonService {

    OtpApi create(String username, String email);

    void validate(String username, String otpCode) throws GenericException;

}
