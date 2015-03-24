package cn.com.janssen.dsr.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private MailSender mailSender;

    public void sendSimpleMail(String mailAddress) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("yanglifan@localhost");
        msg.setTo(mailAddress);
        msg.setSubject("Test mail");
        msg.setText("test test");
        mailSender.send(msg);
    }
}
