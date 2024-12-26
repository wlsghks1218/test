package org.codesync.mapper;

import java.util.List;

import org.codesync.domain.GanttVO;

public interface GanttMapper {
	public int createGantt(GanttVO vo);
	public List<GanttVO> getGanttDataByProjectNo(int projectNo);
	public int deleteGantt(int ganttNo);
	public int updateGantt(GanttVO vo);
}
