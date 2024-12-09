package org.codesync.service;

import java.util.List;

import org.codesync.domain.DocsColumnVO;
import org.codesync.domain.ProjectVO;

public interface DocsService {
	public ProjectVO getProjectNoByWrapperNo(int wrapperNo);
	public List<DocsColumnVO> getColumns(int wrapperNo);
}
