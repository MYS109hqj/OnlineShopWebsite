package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    // 从配置文件注入发件人邮箱
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);  // 设置收件人
        message.setSubject(subject);  // 设置邮件主题
        message.setText(body);  // 设置邮件内容
        message.setFrom(fromEmail);  // 设置发件人邮箱

        // 输出邮件发送相关信息
        logger.info("发件人邮箱: {}", fromEmail);  // 输出发件人邮箱
        logger.info("发送邮件到: {}", to);  // 输出收件人邮箱
        logger.info("邮件主题: {}", subject);  // 输出邮件主题
        logger.info("邮件内容: {}", body);  // 输出邮件内容

        // 发送邮件
        emailSender.send(message);
    }
}

