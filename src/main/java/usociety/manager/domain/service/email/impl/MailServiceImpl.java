package usociety.manager.domain.service.email.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import usociety.manager.domain.service.email.MailService;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void send(String email, String content) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("U Society.");
        msg.setText(content);
        javaMailSender.send(msg);
    }

    @Override
    public void sendOtp(String email, String otpCode) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("U Society - Please verify your account.");
        msg.setText(String.format("This's your verification code: %s.", otpCode));
        javaMailSender.send(msg);
    }

}
