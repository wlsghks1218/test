package org.codesync.service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.codesync.domain.UserDTO;
import org.codesync.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService{
	
	@Autowired
	private MemberMapper mapper;
	
    @Autowired
    private JavaMailSender mailSender;

    private final Map<String, String> verificationCodeStorage = new ConcurrentHashMap<>();

	// 회원가입 시 아이디 중복 여부 판별
	@Override
	public boolean isUsernameDuplicate(String userId) {
		int result = mapper.isUsernameDuplicate(userId); 
		return result>0;
	}
	
    // 인증 코드 생성 메서드
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
	
    // 인증 이메일 전송 (인증 코드 방식)
    @Override
    public String sendVerificationEmail(String email) throws MessagingException {
        String verificationCode = generateVerificationCode();

        verificationCodeStorage.put(email, verificationCode);

        String subject = "Email Verification Code";
        String content = "<p>Thank you for registering.</p>"
                + "<p>Your verification code is:</p>"
                + "<h2>" + verificationCode + "</h2>"
                + "<p>Please enter this code to complete your registration.</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);

        return verificationCode;
    }
    
    @Override
    public boolean registerUser(UserDTO userDTO) {
    	int result = mapper.registerUser(userDTO); 
    	return result>0;
    }
}