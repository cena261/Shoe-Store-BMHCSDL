package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class EmailService {

    final JavaMailSender javaMailSender;

    @Value("${mail.from}")
    String fromEmail;

    @Value("${mail.from-name}")
    String fromName;

    @Value("${mail.frontend-url}")
    String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(String.format("%s <%s>", fromName, fromEmail));
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request - ShoeStore");

            String resetLink = String.format("%s/reset-password?email=%s&token=%s",
                    frontendUrl, toEmail, resetToken);

            String htmlContent = buildPasswordResetEmailHtml(toEmail, resetLink, resetToken);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Password reset email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private String buildPasswordResetEmailHtml(String email, String resetLink, String resetToken) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333333;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        .container {
                            background-color: #f9f9f9;
                            border-radius: 10px;
                            padding: 30px;
                            border: 1px solid #dddddd;
                        }
                        .header {
                            text-align: center;
                            margin-bottom: 30px;
                        }
                        .header h1 {
                            color: #2c3e50;
                            margin: 0;
                        }
                        .content {
                            background-color: #ffffff;
                            padding: 25px;
                            border-radius: 8px;
                            margin-bottom: 20px;
                        }
                        .button {
                            display: inline-block;
                            padding: 12px 30px;
                            background-color: #3498db;
                            color: #ffffff;
                            text-decoration: none;
                            border-radius: 5px;
                            font-weight: bold;
                            text-align: center;
                            margin: 20px 0;
                        }
                        .button:hover {
                            background-color: #2980b9;
                        }
                        .token-box {
                            background-color: #ecf0f1;
                            padding: 15px;
                            border-radius: 5px;
                            font-family: 'Courier New', monospace;
                            word-break: break-all;
                            margin: 15px 0;
                        }
                        .footer {
                            text-align: center;
                            color: #7f8c8d;
                            font-size: 12px;
                            margin-top: 20px;
                        }
                        .warning {
                            color: #e74c3c;
                            font-size: 14px;
                            margin-top: 15px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>ShoeStore</h1>
                        </div>
                        <div class="content">
                            <h2>Password Reset Request</h2>
                            <p>Hello,</p>
                            <p>We received a request to reset the password for your account associated with <strong>%s</strong>.</p>
                            <p>Click the button below to reset your password:</p>
                            <div style="text-align: center;">
                                <a href="%s" class="button">Reset Password</a>
                            </div>
                            <p>Or copy and paste this link into your browser:</p>
                            <div class="token-box">%s</div>
                            <p>Alternatively, you can use this reset token:</p>
                            <div class="token-box">%s</div>
                            <p class="warning"><strong>Important:</strong> This link will expire in 15 minutes. If you did not request a password reset, please ignore this email.</p>
                        </div>
                        <div class="footer">
                            <p>This is an automated email from ShoeStore. Please do not reply to this message.</p>
                            <p>&copy; 2025 ShoeStore. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """, email, resetLink, resetLink, resetToken);
    }
}
