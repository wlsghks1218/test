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
import org.codesync.mapper.ProjectMapper;
import org.codesync.security.domain.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j;

@Log4j
@CrossOrigin(origins = "*")
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler{
	
	@Autowired
	private MemberMapper mapper;
	
	@Autowired
	private ProjectMapper pmapper;
	
	@Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
				Authentication authentication) throws IOException, ServletException {
			log.warn("authentication 객체 꼬라지 보세여 : " + authentication);
			log.warn("아이고 죽겠네" + request.getAttribute("remember-me"));
			boolean rememberMeAttribute = (Boolean)request.getAttribute("remember-me");
	       	String userId = authentication.getName();
	       	
	        HttpSession session = request.getSession();
	        String projectToken = (String) session.getAttribute("token");
	        log.warn("token info : " + projectToken);
	        if(projectToken != null) {
	        	Object principal = authentication.getPrincipal();
	        	CustomUser customUser = (CustomUser) principal;
	        	int userNo = customUser.getUser().getUserNo();
	        	log.warn("로그인 유저의 userNo: " + userNo);
	        	
	            int result4 = pmapper.chkProjectExist(projectToken);
	            if(result4 == 0) {
	            	session.removeAttribute("token");
	            	response.sendRedirect("http://localhost:3000/invalidProject");
	            	return;
	            }
	        	
	        	int result = mapper.getProjectCount(userNo);
	        	if(result >= 3) {
	        		session.removeAttribute("token");
	        		response.sendRedirect("http://localhost:3000/projectLimit");
	        	}
	        	
	        	int projectNo = pmapper.getProjectNoByToken(projectToken);
	        	
	        	Map<String, Integer> params = new HashMap<>();
	        	params.put("userNo", userNo);
	        	params.put("projectNo", projectNo);
	        	int result3 = pmapper.chkProjectJoin(params);
	        	if(result3 > 0) {
	        		session.removeAttribute("token");
	        		response.sendRedirect("http://localhost:3000/alreadyJoined");
	        	}else {
	        		int result2 = pmapper.joinProjectByToken(params);
	        		session.removeAttribute("token");
	        		response.sendRedirect("http://localhost:3000/");
	        	}
	        }
	        
			
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
	        
	        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
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
