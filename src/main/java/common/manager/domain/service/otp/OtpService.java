package common.manager.domain.service.otp;

import common.manager.app.api.OtpApi;
import common.manager.domain.exception.GenericException;
import common.manager.domain.service.common.CommonService;

public interface OtpService extends CommonService {

    OtpApi create(String username);

    void validate(String username, String otpCode) throws GenericException;

}
