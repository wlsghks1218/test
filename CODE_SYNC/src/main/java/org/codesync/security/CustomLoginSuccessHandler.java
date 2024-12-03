package org.codesync.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codesync.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import lombok.extern.log4j.Log4j;

@Log4j
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler{
	
	@Autowired
	private MemberMapper mapper;
	
	@Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
				Authentication authentication) throws IOException, ServletException {
	        String username = authentication.getName(); // 로그인된 사용자 이름
	        String series = UUID.randomUUID().toString(); // 고유 시리즈 값 생성
	        String token = UUID.randomUUID().toString(); // 고유 토큰 값 생성
	        
	        Map<String, Object> params = new HashMap();
	        params.put("username", username);
	        params.put("series", series);
	        params.put("token", token);
			
			log.warn("Login Success");
			
//			int result1 = mapper.deleteRememberMe(username);
//			int result2 = mapper.insertRememberMe(params);
			
	        // 'remember-me' 쿠키 생성
	        javax.servlet.http.Cookie rememberMeCookie = new javax.servlet.http.Cookie("remember-me", series + ":" + token);
	        rememberMeCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
	        rememberMeCookie.setHttpOnly(true); // 보안 설정
	        rememberMeCookie.setPath("/"); // 애플리케이션 전체에서 사용
	        response.addCookie(rememberMeCookie);
			
			request.getSession().setMaxInactiveInterval(7 * 24 * 60 * 60);
			List<String> roleNames = new ArrayList<String>();
			
			authentication.getAuthorities().forEach(auth ->{
				roleNames.add(auth.getAuthority());
			});
			log.warn("ROLE NAME : " + roleNames);
			
			if(roleNames.contains("ROLE_ADMIN")) {
				response.sendRedirect("/sample/admin");
				return;
			}
			
			if(roleNames.contains("ROLE_MEMBER")) {
				response.sendRedirect("/sample/member");
				return;
			}
			response.sendRedirect("/");
		}
}
