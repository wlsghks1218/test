package org.codesync.service;

import java.util.List;

import org.codesync.domain.ChatContentVO;
import org.codesync.domain.CodeSyncChatContentVO;
import org.codesync.mapper.CodeSyncChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class CodeSyncChatServiceImpl implements CodeSyncChatService{
	
	@Autowired
	CodeSyncChatMapper mapper;
	
@Override
public List<CodeSyncChatContentVO> getChatHistory(String codeSyncNo) {
	// TODO Auto-generated method stub
	return mapper.getChatHistory(codeSyncNo);
}
@Override
public int insertChatContent(CodeSyncChatContentVO message) {
	log.warn(message.getCodeSyncNo());
	log.warn(message.getChatTime());
	log.warn(message.getContent());
	log.warn(message.getUserNo());
	return mapper.insertChatContent(message);
}

}
