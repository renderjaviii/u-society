package usociety.manager.domain.service.email.impl;

import static java.lang.Boolean.TRUE;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.email.MailService;

@Service
public class SimpleMailServiceImpl implements MailService {

    private static final String SIGN_IN_MESSAGE = "<html><body><p> Este es tu código de verificación: <b>%s</b>. Ingrésalo en la página para continuar con el registro.</p></body></html>";
    private static final String WELCOME_SUBJECT = "Bienvenido a U Society - Verificación de cuenta.";
    private static final String SIMPLE_SUBJECT = "U Society.";
    private static final String SENDING_EMAIL_ERROR_CODE = "ERROR_SENDING_EMAIL";

    private final JavaMailSender javaMailSender;

    @Autowired
    public SimpleMailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void send(String email, String content, boolean isHtml) throws GenericException {
        try {
            if (isHtml) {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setSubject(SIMPLE_SUBJECT);
                helper.setText(content, TRUE);
                helper.setTo(email);
                javaMailSender.send(message);
            } else {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setSubject(SIMPLE_SUBJECT);
                msg.setText(content);
                msg.setTo(email);
                javaMailSender.send(msg);
            }
        } catch (MessagingException e) {
            throw new GenericException(e.getMessage(), SENDING_EMAIL_ERROR_CODE);
        }
    }

    @Override
    public void sendOtp(String email, String otpCode) throws GenericException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setSubject(WELCOME_SUBJECT);
            helper.setText(String.format(SIGN_IN_MESSAGE, otpCode), TRUE);
            helper.setTo(email);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new GenericException(e.getMessage(), SENDING_EMAIL_ERROR_CODE);
        }

    }

}
