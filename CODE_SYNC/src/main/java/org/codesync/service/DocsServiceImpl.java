package org.codesync.service;

import java.util.List;
import java.util.Map;

import org.codesync.domain.DocsColumnVO;
import org.codesync.domain.DocsVO;
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
	    List<DocsColumnVO> voList = mapper.getColumns(wrapperNo);

	    for (DocsColumnVO vo : voList) {
	        List<DocsVO> files = mapper.getFiles(vo.getColumnNo());
	        vo.setVoList(files);
	    }

	    return voList; // 파일 정보를 포함한 컬럼 리스트 반환
	}
	
	@Override
	public int saveColumn(DocsColumnVO vo) {
		int result = mapper.saveColumn(vo);
		return vo.getColumnNo();
	}
	
	@Override
	public int insertFile(DocsVO vo) {
		return mapper.insertFile(vo);
	}
	
	@Override
	public int fileExist(String docsName) {
		return mapper.fileExist(docsName);
	}
	
	@Override
	public int updateFile(DocsVO vo) {
		return mapper.updateFile(vo);
	}
	
	@Override
	public int checkColumnExist(DocsColumnVO vo) {
		return mapper.checkColumnExist(vo);
	}
	
	@Override
	public int updateColumn(DocsColumnVO vo) {
		return mapper.updateColumn(vo);
	}
	
	@Override
	public int deleteFile(String uploadPath) {
		return mapper.deleteFile(uploadPath);
	}
	
	@Override
	public int deleteColumn(Map<String, Integer> params) {
		return mapper.deleteColumn(params);
	}
	
}
