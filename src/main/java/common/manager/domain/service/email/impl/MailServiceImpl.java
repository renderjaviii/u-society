package common.manager.domain.service.email.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import common.manager.domain.service.email.MailService;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendOtp(String email, String otpCode) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Spring Boot - Please verify your account.");
        msg.setText(String.format("This is the verification code: %s.", otpCode));
        javaMailSender.send(msg);
    }

}
