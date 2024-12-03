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
	       	String userId = authentication.getName(); // 로그인된 사용자 이름
			
	        if (rememberMeAttribute) {
	        	log.warn("여기타는거임");
	            // remember-me 테이블에 저장
	            String series = UUID.randomUUID().toString(); // 고유 시리즈 값 생성
	            String token = UUID.randomUUID().toString(); // 고유 토큰 값 생성

	            Map<String, Object> params = new HashMap<>();
	            params.put("userId", userId);
	            params.put("series", series);
	            params.put("token", token);

	            // 기존 remember-me 데이터 삭제 및 새로 삽입
	            mapper.deleteRememberMe(userId); // 기존 데이터 삭제
	            mapper.insertRememberMe(params);  // 새 데이터 삽입

	            // remember-me 쿠키 생성
	            javax.servlet.http.Cookie rememberMeCookie = new javax.servlet.http.Cookie("remember-me", series + ":" + token);
	            rememberMeCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
	            rememberMeCookie.setHttpOnly(true); // 보안 설정
	            rememberMeCookie.setPath("/"); // 애플리케이션 전체에서 사용
	            response.addCookie(rememberMeCookie);
	        }

	        // 사용자 정보 및 권한 가져오기
	        Object principal = authentication.getPrincipal();
	        List<String> roleNames = new ArrayList<>();
	        authentication.getAuthorities().forEach(auth -> roleNames.add(auth.getAuthority()));

	        log.warn("ROLE NAME : " + roleNames);

	        // JSON 응답 작성
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
