package org.codesync.service;

import java.util.List;

import org.codesync.domain.ArrowVO;
import org.codesync.domain.ChatContentVO;
import org.codesync.domain.ErdHistoryVO;
import org.codesync.domain.MemoVO;
import org.codesync.domain.TableFieldsVO;
import org.codesync.domain.TableVO;

public interface ErdService {

	public String getUserId(String userNo);

	public List<ChatContentVO> getChatHistory(String erdNo);

	public int createTable(TableVO tvo);

	public List<TableVO> getTables(String erdNo);

	public int deleteTable(TableVO tvo);

	public int updateTablePosition(TableVO tvo);

	public int createMemo(MemoVO mvo);

	public List<MemoVO> getMemos(String erdNo);

	public int deleteMemo(MemoVO mvo);

	public int updateMemo(MemoVO mvo);

	public int updateMemoPosition(MemoVO mvo);

	public int createArrow(ArrowVO avo);

	public List<ArrowVO> getArrows(String erdNo);

	public int deleteArrow(ArrowVO avo);

	public int updateTableName(TableVO tvo);

	public int insertFields(TableFieldsVO fvo);

	public List<TableFieldsVO> getTableFields(String id);

	public int deleteAllFields(TableFieldsVO fvo);

	public int deleteField(TableFieldsVO fvo);

	public int updateFields(TableFieldsVO fvo);

	public int deletePrimary(TableFieldsVO fvo);

	public int updateArrow(ArrowVO avo);

	public int addHistory(ErdHistoryVO history);

	public List<ErdHistoryVO> getHistories(int erdNo);

}
