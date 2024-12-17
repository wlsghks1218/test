package org.codesync.service;

import java.util.List;

import org.codesync.domain.ArrowVO;
import org.codesync.domain.ChatContentVO;
import org.codesync.domain.MemoVO;
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

}
