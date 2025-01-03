package org.codesync.service;

import java.util.List;
import java.util.Map;

import org.codesync.domain.DocsColumnVO;
import org.codesync.domain.DocsHistoryVO;
import org.codesync.domain.DocsVO;
import org.codesync.domain.ProjectVO;
import org.springframework.web.bind.annotation.RequestParam;

public interface DocsService {
	public ProjectVO getProjectNoByWrapperNo(int wrapperNo);
	public List<DocsColumnVO> getColumns(int wrapperNo);
	public int saveColumn(DocsColumnVO vo);
	public int checkColumnExist(DocsColumnVO vo);
	public int insertFile(DocsVO vo);
	public int fileExist(String docsName);
	public int updateFile(DocsVO vo);
	public int updateColumn(DocsColumnVO vo);
	public int deleteFile(String uploadPath);
	public int deleteColumn(Map<String, Integer> params);
	public int uploadHistory(Map<String, Object> params);
	public int deleteHistory(Map<String, Object> params);
	public List<DocsHistoryVO> getDocsHistory(Map<String, Integer> params);
}
