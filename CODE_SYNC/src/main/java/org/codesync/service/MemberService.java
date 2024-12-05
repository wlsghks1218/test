package org.codesync.service;

import java.util.List;

import javax.mail.MessagingException;

import org.codesync.domain.UserDTO;

public interface MemberService {
	public boolean isUsernameDuplicate(String userId);
    public String sendVerificationEmail(String email) throws MessagingException;
    public boolean registerUser(UserDTO userDTO);
    public List<UserDTO> getAllUsers();
}
