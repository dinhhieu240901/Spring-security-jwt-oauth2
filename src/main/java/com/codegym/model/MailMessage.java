package com.codegym.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.mail.internet.MimeMessage;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailMessage {
    private String subject;
    private String content;

}
