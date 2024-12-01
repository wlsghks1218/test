package org.kacang.service;

import org.kacang.mapper.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService{
	
	@Autowired
	private TestMapper mapper;
	
	@Override
	public int insertTest() {
		return mapper.insertTest();
	}
}
