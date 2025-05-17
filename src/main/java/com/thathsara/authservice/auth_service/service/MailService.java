package com.thathsara.authservice.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String subject, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(buildOtpEmailHtml(otp), true);

            // ✅ Important: send the email
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildOtpEmailHtml(String otp) {
        return "<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + "<head>\n"
                + "  <meta charset=\"UTF-8\">\n"
                + "  <title>OTP Verification</title>\n"
                + "  <style>\n"
                + "    body { margin:0; padding:0; background-color:#f3f4f6; font-family:'Segoe UI', sans-serif; color:#374151; }\n"
                + "    .container { max-width:480px; margin:30px auto; background-color:#fff; border-radius:12px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.08); }\n"
                + "    .header { background-color:#1e40af; padding:24px; text-align:center; color:#fff; }\n"
                + "    .header h1 { margin:0; font-size:24px; }\n"
                + "    .body { padding:24px; text-align:center; }\n"
                + "    .body h2 { color:#111827; font-size:20px; margin-bottom:8px; }\n"
                + "    .otp-code { display:inline-block; background-color:#1e3a8a; color:#fff; font-size:28px; letter-spacing:6px; padding:14px 24px; border-radius:8px; margin:16px 0; }\n"
                + "    .body p { font-size:14px; color:#6b7280; margin:16px 0 0 0; }\n"
                + "    .footer { background-color:#f9fafb; padding:16px; text-align:center; font-size:12px; color:#9ca3af; }\n"
                + "    .footer a { color:#3b82f6; text-decoration:none; font-weight:500; }\n"
                + "  </style>\n"
                + "</head>\n"
                + "<body>\n"
                + "  <div class=\"container\">\n"
                + "    <div class=\"header\">\n"
                + "      <h1>🌩️ StormGate AuthService</h1>\n"
                + "    </div>\n"
                + "    <div class=\"body\">\n"
                + "      <h2>Your One-Time Password (OTP)</h2>\n"
                + "      <div class=\"otp-code\">" + otp + "</div>\n"
                + "      <p>This code is valid for the next 5 minutes. Do not share it with anyone.</p>\n"
                + "    </div>\n"
                + "    <div class=\"footer\">\n"
                + "      If you didn’t request this code, please <a href=\"mailto:support@stormgate.io\">contact support</a>.<br/>\n"
                + "      © 2025 StormGate Inc.\n"
                + "    </div>\n"
                + "  </div>\n"
                + "</body>\n"
                + "</html>";
    }
}
