package org.codesync.mapper;

import java.util.List;

import org.codesync.domain.DocsColumnVO;
import org.codesync.domain.ProjectVO;

public interface DocsMapper {
	public int getProjectNoByWrapperNo(int wrapperNo);
	public ProjectVO getProjectByProjectNo(int projectNo);
	public List<DocsColumnVO> getColumns(int wrapperNo);
}
