package org.codesync.service;

import org.codesync.domain.ChatContentVO;
import org.codesync.mapper.ChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class ChatServiceImpl implements ChatService {
	
	@Autowired
	public ChatMapper mapper;
	
	@Override
	public int insertChatContent(ChatContentVO message) {
		log.warn(message.getErdNo());
		log.warn(message.getChatTime());
		log.warn(message.getContent());
		log.warn(message.getUserNo());
		return mapper.insertChatContent(message);
	}

}
