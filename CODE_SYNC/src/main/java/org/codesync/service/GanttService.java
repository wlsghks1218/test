package org.codesync.service;

import java.util.List;

import org.codesync.domain.DocsHistoryVO;
import org.codesync.domain.GanttVO;

public interface GanttService {
	public int createGantt(GanttVO vo);
	public List<GanttVO> getGanttDataByProjectNo(int projectNo);
	public int deleteGantt(int ganttNo);
	public int updateGantt(GanttVO vo);
	public List<DocsHistoryVO> getDocsHistoryForGantt(int projectNo);
}