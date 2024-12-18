package org.codesync.mapper;

import java.util.List;
import java.util.Map;

import org.codesync.domain.DocsColumnVO;
import org.codesync.domain.DocsHistoryVO;
import org.codesync.domain.DocsVO;
import org.codesync.domain.ProjectVO;

public interface DocsMapper {
	public int getProjectNoByWrapperNo(int wrapperNo);
	public ProjectVO getProjectByProjectNo(int projectNo);
	public List<DocsColumnVO> getColumns(int wrapperNo);
	public int saveColumn(DocsColumnVO vo);
	public int checkColumnExist(DocsColumnVO vo);
	public int insertFile(DocsVO vo);
	public int fileExist(String docsName);
	public int updateFile(DocsVO vo);
	public List<DocsVO> getFiles(int columnNo);
	public int updateColumn(DocsColumnVO vo);
	public int deleteFile(String uploadPath);
	public int deleteColumn(Map<String, Integer> params);
	public int uploadHistory(Map<String, Object> params);
	public int deleteHistory(Map<String, Object> params);
	public List<DocsHistoryVO> getDocsHistory(Map<String, Integer> params);
}
