package nil.webparser.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EmailService {

    @Value("${server.host}")
    private String host;

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String name) {

        String verificationCode = UUID.randomUUID().toString();
        String verificationLink = host + "/confirmRegistration?verificationCode=" + verificationCode;
        String template = "Dear, %s\nTo accomplish registration, confirm specified email. \nYour verification link: %s";
        String text = String.format(template, name, verificationLink, verificationLink);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("thanks for registration. please, confirm your email");
        message.setText(text);
        emailSender.send(message);
    }
}
