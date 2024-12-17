package org.codesync.mapper;

import java.util.List;
import java.util.Map;

import org.codesync.domain.UserDTO;

public interface MemberMapper {
	public int isUsernameDuplicate(String userId);
    public int registerUser(UserDTO userDTO);
    public UserDTO read(String userId);
	public int deleteRememberMe(String userId);
	public int insertRememberMe(Map<String, Object> params);
	public List<UserDTO> getAllUsers();
	public int updateUserId(Map<String, String> requestData);
	public UserDTO getUserInfo(int userNo);
    public int chkPassword(Map<String, String> requestData);
    public int updatePassword(Map<String, Object> params);
    public int updateEmail(Map<String, String> requestData);
    public int getProjectCount(int userNo);
}

