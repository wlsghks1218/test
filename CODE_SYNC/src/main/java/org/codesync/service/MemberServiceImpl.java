package org.codesync.service;

import org.codesync.domain.UserDTO;
import org.codesync.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService{
	
	@Autowired
	private MemberMapper mapper;
	
	// 회원가입 시 아이디 중복 여부 판별
	@Override
	public boolean isUsernameDuplicate(String userId) {
		return mapper.isUsernameDuplicate(userId);
	}
}
