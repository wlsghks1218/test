package org.codesync.mapper;

import java.util.Map;

import org.codesync.domain.UserDTO;

public interface MemberMapper {
	public int isUsernameDuplicate(String userId);
    public int registerUser(UserDTO userDTO);
    public UserDTO read(String userId);
	public int deleteRememberMe(String userId);
	public int insertRememberMe(Map<String, Object> params);
}