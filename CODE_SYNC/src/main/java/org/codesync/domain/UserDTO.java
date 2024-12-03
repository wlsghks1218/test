package org.codesync.domain;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Data;

@Data
public class UserDTO {
	private static final long serialVersionUID = 1L;
	 
	int userNo, authAdmin;
	String userId, userPw, userEmail, snsToken, snsProvider;
	Date userRegDate;
}
