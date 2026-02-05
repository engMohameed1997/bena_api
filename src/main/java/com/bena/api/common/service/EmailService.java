package com.bena.api.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * خدمة إرسال البريد الإلكتروني
 * ملاحظة: هذه نسخة مبسطة. في الإنتاج، استخدم JavaMailSender أو خدمة خارجية مثل SendGrid
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Value("${app.email.from:noreply@bena-app.com}")
    private String fromEmail;
    
    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;
    
    /**
     * إرسال بريد إعادة تعيين كلمة المرور
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken, String userName) {
        String subject = "إعادة تعيين كلمة المرور - تطبيق بناء";
        String resetLink = "bena-app://reset-password?token=" + resetToken;
        
        String body = String.format("""
            مرحباً %s،
            
            لقد طلبت إعادة تعيين كلمة المرور الخاصة بك.
            
            رمز التحقق: %s
            
            أو انقر على الرابط التالي:
            %s
            
            هذا الرمز صالح لمدة ساعة واحدة فقط.
            
            إذا لم تطلب إعادة تعيين كلمة المرور، يرجى تجاهل هذا البريد.
            
            مع تحيات،
            فريق تطبيق بناء
            """, userName, resetToken, resetLink);
        
        sendEmail(toEmail, subject, body);
    }
    
    /**
     * إرسال بريد تأكيد الحساب
     */
    public void sendVerificationEmail(String toEmail, String verificationToken, String userName) {
        String subject = "تأكيد حسابك - تطبيق بناء";
        String verifyLink = "bena-app://verify-email?token=" + verificationToken;
        
        String body = String.format("""
            مرحباً %s،
            
            شكراً لتسجيلك في تطبيق بناء!
            
            رمز التحقق: %s
            
            أو انقر على الرابط التالي لتأكيد حسابك:
            %s
            
            مع تحيات،
            فريق تطبيق بناء
            """, userName, verificationToken, verifyLink);
        
        sendEmail(toEmail, subject, body);
    }
    
    /**
     * إرسال بريد ترحيبي
     */
    public void sendWelcomeEmail(String toEmail, String userName) {
        String subject = "مرحباً بك في تطبيق بناء!";
        
        String body = String.format("""
            مرحباً %s،
            
            أهلاً وسهلاً بك في تطبيق بناء!
            
            يمكنك الآن:
            - تصميم منزلك بالذكاء الاصطناعي
            - حساب تكلفة البناء
            - متابعة خطوات البناء
            - الاطلاع على أسعار المواد
            
            نتمنى لك تجربة ممتعة!
            
            مع تحيات،
            فريق تطبيق بناء
            """, userName);
        
        sendEmail(toEmail, subject, body);
    }
    
    /**
     * إرسال رمز OTP عبر SMS
     */
    public void sendSmsOtp(String phoneNumber, String otp) {
        // في وضع التطوير، نطبع الرسالة في الـ console
        logger.info("========== SMS OTP (Development Mode) ==========");
        logger.info("To: {}", phoneNumber);
        logger.info("OTP Code: {}", otp);
        logger.info("Message: رمز التحقق الخاص بك هو: {}", otp);
        logger.info("================================================");
        
        // TODO: في الإنتاج، استخدم خدمة SMS مثل Twilio أو Firebase
        // twilioService.sendSms(phoneNumber, "رمز التحقق الخاص بك هو: " + otp);
    }
    
    /**
     * إرسال البريد الفعلي
     */
    private void sendEmail(String to, String subject, String body) {
        if (!emailEnabled) {
            // في وضع التطوير، نطبع البريد في الـ console
            logger.info("========== EMAIL (Development Mode) ==========");
            logger.info("To: {}", to);
            logger.info("Subject: {}", subject);
            logger.info("Body:\n{}", body);
            logger.info("==============================================");
            return;
        }
        
        // TODO: في الإنتاج، استخدم JavaMailSender
        // mailSender.send(message);
        logger.info("Email sent to: {}", to);
    }
}
