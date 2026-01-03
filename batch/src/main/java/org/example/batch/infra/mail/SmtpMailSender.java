package org.example.batch.infra.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpMailSender {

    private final JavaMailSender sender;

    public void sendHtml(String from, String to, String subject, String html) {
        try {
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            sender.send(msg);
        } catch (Exception e) {
            throw new IllegalStateException("[MAIL] 메일 전송 실패: to=" + to, e);
        }
    }
}
