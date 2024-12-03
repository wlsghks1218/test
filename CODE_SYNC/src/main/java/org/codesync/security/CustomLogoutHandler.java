package org.codesync.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codesync.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class CustomLogoutHandler implements LogoutHandler {

    @Autowired
    private MemberMapper mapper;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }

        if (authentication != null) {
            String username = authentication.getName();
            System.out.println("Logging out user: " + username);

            if (username != null) {
                try {
                    mapper.deleteRememberMe(username);
                } catch (Exception e) {
                    System.err.println("Failed to delete remember-me token for user: " + username);
                    e.printStackTrace();
                }
            }

            SecurityContextHolder.clearContext();
        } else {
            System.out.println("Authentication is null during logout.");
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            try {
                session.invalidate();
                System.out.println("Session invalidated.");
            } catch (IllegalStateException e) {
                System.err.println("Failed to invalidate session.");
                e.printStackTrace();
            }
        }

        if (response != null) {
            javax.servlet.http.Cookie rememberMeCookie = new javax.servlet.http.Cookie("remember-me", null);
            rememberMeCookie.setMaxAge(0); // 즉시 삭제
            rememberMeCookie.setHttpOnly(true);
            rememberMeCookie.setPath("/");
            response.addCookie(rememberMeCookie);
            System.out.println("Remember-me cookie cleared.");
        }
    }
}
