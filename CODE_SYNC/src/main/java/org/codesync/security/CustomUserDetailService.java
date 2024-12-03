package org.codesync.security;

import org.codesync.domain.UserDTO;
import org.codesync.mapper.MemberMapper;
import org.codesync.security.domain.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.extern.log4j.Log4j;

@Log4j
public class CustomUserDetailService implements UserDetailsService{
	
	@Autowired
	private MemberMapper mapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    log.warn("username : " + username);
	    UserDTO vo = null;
//	    mapper.read(username);
	    if (vo == null) {
	        log.warn(username+"유저 없음");
	        throw new UsernameNotFoundException(username + "유저 없음");
	    }
	    log.warn("user : " + vo);
	    return new CustomUser(vo);
	}
}
