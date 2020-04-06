package com.prefer_music_store.app.util;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailUtils {
    @Resource(name = "mailSender")
    private JavaMailSender mailSender;

    public void sendMail(String to, String title, String content, boolean html) {
        try {
            MimeMessage msg = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "utf-8");

            helper.setFrom("jayur830@gmail.com");
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content, html);

            this.mailSender.send(msg);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}