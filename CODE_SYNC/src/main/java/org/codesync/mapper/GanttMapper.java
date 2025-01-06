package org.codesync.mapper;

import java.util.List;

import org.codesync.domain.CodeSyncHistoryVO;
import org.codesync.domain.DocsHistoryVO;
import org.codesync.domain.ErdHistoryVO;
import org.codesync.domain.GanttVO;

public interface GanttMapper {
	public int createGantt(GanttVO vo);
	public List<GanttVO> getGanttDataByProjectNo(int projectNo);
	public int deleteGantt(int ganttNo);
	public int updateGantt(GanttVO vo);
	public List<DocsHistoryVO> getDocsHistoryForGantt(int projectNo);
	public int getErdNoForGanttHistory(int projectNo);
	public List<ErdHistoryVO> getErdHistoryForGantt(int erdNo);
	public List<CodeSyncHistoryVO> getCodeHistoryForGantt(int projectNo);
}
