package org.codesync.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class UserDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	int userNo, authAdmin;
	String userId, userPw, userEmail, snsToken, snsProvider;
	Date userRegDate;
	
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer projectCount; 
}
