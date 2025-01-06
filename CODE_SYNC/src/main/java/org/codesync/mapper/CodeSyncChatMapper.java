package org.codesync.mapper;

import java.util.List;

import org.codesync.domain.CodeSyncChatContentVO;

public interface CodeSyncChatMapper {

	List<CodeSyncChatContentVO> getChatHistory(String codeSyncNo);

	int insertChatContent(CodeSyncChatContentVO message);


}
