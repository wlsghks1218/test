package org.codesync.mapper;

import org.codesync.domain.UserDTO;

public interface MemberMapper {
	public int isUsernameDuplicate(String userId);
    public int registerUser(UserDTO userDTO);
    public UserDTO read(String userId);
}
