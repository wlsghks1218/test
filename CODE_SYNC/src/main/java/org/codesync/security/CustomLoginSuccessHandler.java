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
import javax.servlet.http.HttpSession;

import org.codesync.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j;

@Log4j
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler{
	
	@Autowired
	private MemberMapper mapper;
	
	@Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
				Authentication authentication) throws IOException, ServletException {
			log.warn("authentication 객체 꼬라지 보세여 : " + authentication);
			log.warn("아이고 죽겠네" + request.getAttribute("remember-me"));
			boolean rememberMeAttribute = (Boolean)request.getAttribute("remember-me");
	       	String userId = authentication.getName();
			
	        if (rememberMeAttribute) {
	        	log.warn("여기타는거임");
	            String series = UUID.randomUUID().toString();
	            String token = UUID.randomUUID().toString();

	            Map<String, Object> params = new HashMap<>();
	            params.put("userId", userId);
	            params.put("series", series);
	            params.put("token", token);

	            mapper.deleteRememberMe(userId);
	            mapper.insertRememberMe(params);

	            // remember-me 쿠키 생성
	            javax.servlet.http.Cookie rememberMeCookie = new javax.servlet.http.Cookie("remember-me", series + ":" + token);
	            rememberMeCookie.setMaxAge(7 * 24 * 60 * 60);
	            rememberMeCookie.setHttpOnly(true);
	            rememberMeCookie.setPath("/");
	            response.addCookie(rememberMeCookie);
	        }

	        // 사용자 정보 및 권한 가져오기
	        Object principal = authentication.getPrincipal();
	        List<String> roleNames = new ArrayList<>();
	        authentication.getAuthorities().forEach(auth -> roleNames.add(auth.getAuthority()));

	        log.warn("ROLE NAME : " + roleNames);
	        
	        HttpSession session = request.getSession();
	        session.setAttribute("userId", userId);
	        session.setAttribute("roles", roleNames);
	        session.setAttribute("principal", principal);

	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");

	        // 응답 데이터 구성
	        HashMap<String, Object> responseData = new HashMap<>();
	        responseData.put("principal", principal);
	        responseData.put("roles", roleNames);

	        // JSON 응답 반환
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.writeValue(response.getWriter(), responseData);
	        
	        // 응답 종료
	        response.getWriter().flush();
	        response.getWriter().close();
	    }
}
