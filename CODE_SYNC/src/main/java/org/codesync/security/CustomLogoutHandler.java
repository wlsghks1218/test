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
	
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Retrieved authentication from SecurityContextHolder: " + authentication);
        }

        if (authentication != null) {
            System.out.println("Logging out user: " + authentication.getName());
            SecurityContextHolder.clearContext();
        } else {
            System.out.println("Authentication is still null during logout.");
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
