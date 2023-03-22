package com.sample.project.common.service.email;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @NonNull
    private JavaMailSender mailSender;

    @NonNull
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 템플릿을 통해 이메일을 발송합니다.
     *
     * @param subject
     * @param to
     * @param templateFileName
     * @param content
     */
    public void sendMail(String subject, String to, String templateFileName, Object content) {
        MimeMessagePreparator message = mimeMessage -> {
            Context context = new Context();

            context.setVariable("content", content);

            String htmlText = templateEngine.process(templateFileName, context);

            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(htmlText, true);
        };

        mailSender.send(message);
    }

}
