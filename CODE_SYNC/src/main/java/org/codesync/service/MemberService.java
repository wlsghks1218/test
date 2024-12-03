package org.codesync.service;

import org.codesync.domain.UserDTO;

public interface MemberService {
	public boolean isUsernameDuplicate(String userId);
}
