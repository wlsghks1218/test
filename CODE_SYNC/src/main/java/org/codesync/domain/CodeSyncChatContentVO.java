package org.codesync.domain;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodeSyncChatContentVO {
	private String code;		//상태코드
	private String codeSyncNo;
	private String userNo;
	private String userId;		//보내는 사람
	private String content;		//대화 내용
	private Timestamp chatTime;		//날짜
	
	@Override
	public String toString() {
	    return "ChatContentVO [code=" + code + ", codeSyncNo=" + codeSyncNo + ", userNo=" + userNo + ", userId=" + userId + ", content=" + content
	            + ", chatTime=" + chatTime + "]";
	}
}
