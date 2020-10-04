package usociety.manager.domain.service.email;

public interface MailService {

    void send(String email, String content);

    void sendOtp(String email, String otpCode);

}
