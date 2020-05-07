package common.manager.domain.service.email;

public interface MailService {

    void sendOtp(String email, String otpCode);

}
