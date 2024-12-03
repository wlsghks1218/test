package org.codesync.controller;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codesync.domain.LoginRequest;
import org.codesync.domain.UserDTO;
import org.codesync.security.CustomLoginSuccessHandler;
import org.codesync.security.CustomLogoutHandler;
import org.codesync.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j;

@Log4j
@RestController
@RequestMapping("/member/*")
@CrossOrigin(origins = "*")
public class MemberController {
	
	@Autowired
	private MemberService service;
	
    @Autowired
    private PasswordEncoder pwencoder;
	
    @PostMapping("/checkUsername")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        log.info("userId는 : " + userId);
        boolean isDuplicate = service.isUsernameDuplicate(userId);
        log.info("유저 중복 여부는 : " + isDuplicate);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/sendVerification")
    public ResponseEntity<Map<String, String>> sendVerification(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("userEmail");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email is required."));
            }
            String verificationCode = service.sendVerificationEmail(email);
            return ResponseEntity.ok(Map.of(
                "message", "Verification email sent successfully.",
                "verificationCode", verificationCode
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", "Failed to send verification email."
            ));
        }
    }
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private CustomLoginSuccessHandler customLoginSuccessHandler;
    
    @Autowired
    private CustomLogoutHandler customLogoutHandler;
    
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody UserDTO userDTO) {
    	userDTO.setUserPw(pwencoder.encode(userDTO.getUserPw()));
    	boolean success = service.registerUser(userDTO);
    	if (success) {
    		return ResponseEntity.ok("회원가입 성공");
    	} else {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패");
    	}
    }

    @PostMapping("/login") // 경로 임의 설정
    public void  login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
    	log.warn("loginRequest : "+ loginRequest);
		    UsernamePasswordAuthenticationToken authToken =
		            new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getUserPw());
		    Authentication auth = authenticationManager.authenticate(authToken);
		    SecurityContextHolder.getContext().setAuthentication(auth);
		    
		    request.setAttribute("remember-me", loginRequest.isRememberMe());
		    log.warn(request.getParameter("remember-me"));
    			
    		// 인증이 성공하면 CustomUserDetailsService가 호출되어 사용자가 반환됨
		    customLoginSuccessHandler.onAuthenticationSuccess(request, response, auth);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> requestBody, HttpServletRequest request, HttpServletResponse response) {
        log.info("여기 타는건가여");
        String userId = requestBody.get("userId"); // 클라이언트에서 전송된 userId 추출
        log.info("로그아웃 요청 userId: " + userId);

        // CustomLogoutHandler 생성 및 로그아웃 처리
        CustomLogoutHandler logoutHandler = new CustomLogoutHandler();
        logoutHandler.logout(request, response, null);

        return ResponseEntity.ok("Logout successful for userId: " + userId);
    }
//    
//    @GetMapping("/login")
//    public String loginPage() {
//        return "forward:/index.html";
//    }
}