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
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
	    log.warn("userId : " + userId);
	    UserDTO vo = mapper.read(userId);
	    if (vo == null) {
	        log.warn(userId+"유저 없음");
	        throw new UsernameNotFoundException(userId + "유저 없음");
	    }
	    log.warn("user : " + vo);
	    return new CustomUser(vo);
	}
}
