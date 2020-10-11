package usociety.manager.domain.service.email;

import javax.mail.MessagingException;

public interface MailService {

    void send(String email, String content, boolean isHtml) throws MessagingException;

    void sendOtp(String email, String otpCode);

}
