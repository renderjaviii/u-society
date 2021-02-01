package usociety.manager.domain.service.email;

import usociety.manager.domain.exception.GenericException;

public interface MailService {

    void send(String email, String content, boolean isHtml) throws GenericException;

    void sendOtp(String email, String otpCode) throws GenericException;

}
