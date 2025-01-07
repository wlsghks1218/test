package org.codesync.service;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.codesync.domain.UserDTO;

public interface MemberService {
	public boolean isUsernameDuplicate(String userId);
    public String sendVerificationEmail(String email) throws MessagingException;
    public boolean registerUser(UserDTO userDTO);
    public List<UserDTO> getAllUsers();
    public int updateUserId(Map<String, String> requestData);
    public UserDTO getUserInfo(int UserNo);
    public int chkPassword(Map<String, String> requestData);
    public int updatePassword(Map<String, Object> params);
    public int updateEmail(Map<String, String> requestData);
    public int getProjectCount(int userNo);
    public List<UserDTO> findId(String email);
    public int chkEmailExistForPassword(Map<String, String> request);
    public int changePassword(Map<String, String> request);
    public int chkEmailExist(String userEmail);
}
