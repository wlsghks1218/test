package org.codesync.service;

import java.util.List;

import org.codesync.domain.ArrowVO;
import org.codesync.domain.ChatContentVO;
import org.codesync.domain.ErdHistoryVO;
import org.codesync.domain.MemoVO;
import org.codesync.domain.ProjectVO;
import org.codesync.domain.TableFieldsVO;
import org.codesync.domain.TableVO;
import org.codesync.mapper.ErdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ErdServiceImpl implements ErdService {

	@Autowired
	ErdMapper mapper;
	
	@Override
	public String getUserId(String userNo) {
		
		return mapper.getUserId(userNo);
	}

	@Override
	public List<ChatContentVO> getChatHistory(String erdNo) {

		return mapper.getChatHistory(erdNo);
	}

	@Override
	public int createTable(TableVO tvo) {
		
		return mapper.createTable(tvo);
	}

	@Override
	public List<TableVO> getTables(String erdNo) {

		return mapper.getTables(erdNo);
	}

	@Override
	public int deleteTable(TableVO tvo) {

		return mapper.deleteTable(tvo);
	}

	@Override
	public int updateTablePosition(TableVO tvo) {

		return mapper.updateTablePosition(tvo);
	}

	@Override
	public int createMemo(MemoVO mvo) {

		return mapper.createMemo(mvo);
	}

	@Override
	public List<MemoVO> getMemos(String erdNo) {

		return mapper.getMemos(erdNo);
	}

	@Override
	public int deleteMemo(MemoVO mvo) {

		return mapper.deleteMemo(mvo);
	}

	@Override
	public int updateMemo(MemoVO mvo) {
		
		return mapper.updateMemo(mvo);
	}

	@Override
	public int updateMemoPosition(MemoVO mvo) {
	
		return mapper.updateMemoPosition(mvo);
	}

	@Override
	public int createArrow(ArrowVO avo) {
		// TODO Auto-generated method stub
		return mapper.createArrow(avo);
	}

	@Override
	public List<ArrowVO> getArrows(String erdNo) {
		// TODO Auto-generated method stub
		return mapper.getArrows(erdNo);
	}

	@Override
	public int deleteArrow(ArrowVO avo) {
		// TODO Auto-generated method stub
		return mapper.deleteArrow(avo);
	}

	@Override
	public int updateTableName(TableVO tvo) {

		return mapper.updateTableName(tvo);
	}

	@Override
	public int insertFields(TableFieldsVO fvo) {
		// TODO Auto-generated method stub
		return mapper.insertFields(fvo);
	}

	@Override
	public List<TableFieldsVO> getTableFields(String id) {
		
		System.out.println("서비스에서 받은 아이디 " + id);
		// TODO Auto-generated method stub
		return mapper.getTableFields(id);
	}

	@Override
	public int deleteAllFields(TableFieldsVO fvo) {
		// TODO Auto-generated method stub
		return mapper.deleteAllFields(fvo);
	}

	@Override
	public int deleteField(TableFieldsVO fvo) {
		// TODO Auto-generated method stub
		return mapper.deleteField(fvo);
	}

	@Override
	public int updateFields(TableFieldsVO fvo) {
		// TODO Auto-generated method stub
		return mapper.updateFields(fvo);
	}

	@Override
	public int deletePrimary(TableFieldsVO fvo) {
		// TODO Auto-generated method stub
		return mapper.deletePrimary(fvo);
	}

	@Override
	public int updateArrow(ArrowVO avo) {
		// TODO Auto-generated method stub
		return mapper.updateArrow(avo);
	}

	@Override
	public int addHistory(ErdHistoryVO history) {
		// TODO Auto-generated method stub
		return mapper.addHistory(history);
	}

	@Override
	public List<ErdHistoryVO> getHistories() {
		// TODO Auto-generated method stub
		return mapper.getHistories();
	}
@Override
public ProjectVO getProject(int erdNo) {
	int projectNo= mapper.getProjectNoByErdNo(erdNo);
	ProjectVO result = mapper.getProject(projectNo);
	return result;
}
}
