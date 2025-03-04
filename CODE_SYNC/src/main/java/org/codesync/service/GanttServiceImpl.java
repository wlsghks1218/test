package org.codesync.service;

import java.util.List;

import org.codesync.domain.CodeSyncHistoryVO;
import org.codesync.domain.DocsHistoryVO;
import org.codesync.domain.ErdHistoryVO;
import org.codesync.domain.GanttVO;
import org.codesync.mapper.GanttMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class GanttServiceImpl implements GanttService{
	
	@Autowired
	private GanttMapper mapper;

	@Override
	public int createGantt(GanttVO vo) {
		return mapper.createGantt(vo);
	}
	
	@Override
	public List<GanttVO> getGanttDataByProjectNo(int projectNo) {
		return mapper.getGanttDataByProjectNo(projectNo);
	}
	
	@Override
	public int deleteGantt(int ganttNo) {
		return mapper.deleteGantt(ganttNo);
	}
	
	@Override
	public int updateGantt(GanttVO vo) {
		return mapper.updateGantt(vo);
	}
	
	@Override
	public List<DocsHistoryVO> getDocsHistoryForGantt(int projectNo) {
		return mapper.getDocsHistoryForGantt(projectNo);
	}
	
	@Override
	public List<ErdHistoryVO> getErdHistoryForGantt(int projectNo) {
		int erdNo = mapper.getErdNoForGanttHistory(projectNo);
		return mapper.getErdHistoryForGantt(erdNo);
	}
	
	@Override
	public List<CodeSyncHistoryVO> getCodeHistoryForGantt(int projectNo) {
		return mapper.getCodeHistoryForGantt(projectNo);
	}
}
