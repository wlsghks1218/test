package org.codesync.service;

import java.util.List;

import org.codesync.domain.ChatContentVO;
import org.codesync.domain.CodeSyncChatContentVO;
import org.springframework.stereotype.Service;


@Service
public interface CodeSyncChatService {

	List<CodeSyncChatContentVO> getChatHistory(String codeSyncNo);

	int insertChatContent(CodeSyncChatContentVO message);


}
