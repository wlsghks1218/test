package org.codesync.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codesync.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import lombok.extern.log4j.Log4j;

@Log4j
public class CustomLogoutHandler implements LogoutHandler {

    @Autowired
    private MemberMapper mapper;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    	String username = request.getParameter("userId");
        log.warn("로그아웃 유저: " + username);

        if (username != null && !username.equals("anonymousUser")) {
            try {
                mapper.deleteRememberMe(username);
                log.warn("remember-me 정보 삭제 완료: " + username);
            } catch (Exception e) {
                log.error("Failed to delete remember-me token for user: " + username, e);
            }
        }

        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            try {
                session.invalidate();
                log.info("Session invalidated.");
            } catch (IllegalStateException e) {
                log.error("Failed to invalidate session.", e);
            }
        }

        if (response != null) {
            javax.servlet.http.Cookie rememberMeCookie = new javax.servlet.http.Cookie("remember-me", null);
            rememberMeCookie.setMaxAge(0);
            rememberMeCookie.setHttpOnly(true);
            rememberMeCookie.setPath("/");
            response.addCookie(rememberMeCookie);
            log.info("Remember-me cookie cleared.");
        }
    }
}
