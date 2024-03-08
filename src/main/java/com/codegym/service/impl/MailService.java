package com.codegym.service.impl;

import com.codegym.model.MailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class MailService {
    @Autowired
    private JavaMailSender sender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String mail;

    private boolean mailSentStatus = false;
    private List<MailMessage> mailsSent = new ArrayList<>();

    public void send(MailMessage message, String to) {
        MimeMessagePreparator preparation = prepare(message, to);
        try {
            sender.send(preparation);
            mailSentStatus = true;
            mailsSent.add(message);
            System.out.println();
        } catch (MailException e) {
            System.out.println("Cant not send mail");
            mailSentStatus = false;
        }

    }

    private MimeMessagePreparator prepare(MailMessage message, String to) {
        return mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(mail);
            helper.setTo(to);
            helper.setSubject(message.getSubject());
            Context context = new Context();
            context.setVariable("message", message.getContent());
            String content = templateEngine.process("mail-template", context);
            helper.setText(content, true);
        };
    }

    public boolean getSendingStatus() {
        return mailSentStatus;
    }


    private String getCodeFromForgetMail() {
        String result = "";
        for (MailMessage message : mailsSent) {
            if (message.getSubject().equals("Code for reset password")) {
                result= message.getContent();
            }
        }
        System.out.println("code " + result);
        return result;
    }

    public boolean validateForgetCode(String code) {
        System.out.println("code1: " + code);
        return getCodeFromForgetMail().equals(code);
//        return Objects.equals(getCodeFromForgetMail(), code);
    }
}
