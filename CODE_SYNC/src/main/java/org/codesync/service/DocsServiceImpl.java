package org.codesync.service;

import java.util.List;

import org.codesync.domain.DocsColumnVO;
import org.codesync.domain.ProjectVO;
import org.codesync.mapper.DocsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class DocsServiceImpl implements DocsService{
	
	@Autowired
	private DocsMapper mapper;
	
	@Override
	@Transactional
	public ProjectVO getProjectNoByWrapperNo(int wrapperNo) {
		int result = mapper.getProjectNoByWrapperNo(wrapperNo);
		log.warn("docs에 출력하는 projectNo : " + result);
		return mapper.getProjectByProjectNo(result);
	}
	
	@Override
	public List<DocsColumnVO> getColumns(int wrapperNo) {
		return mapper.getColumns(wrapperNo);
	}
}
