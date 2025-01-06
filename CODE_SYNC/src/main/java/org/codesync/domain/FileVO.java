package org.codesync.domain;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileVO {
	// 제가 멍청했습니다 다음에는 웹소켓용 VO 반드시 따로 만들겠습니다 다시 한번 복창하겠습니다 제가 멍청했습니다!
	private int fileNo;
	private int folderNo;
	private String fileName;
	private String newName;  // 웹소켓 처리를 위해 추가됨
	private String folderName; // 웹소켓 처리를 위해 추가됨
	private String extension;
	private String content;
	private String filePath;
	private String folderPath;// 웹소켓 처리를 위해 추가됨
	private LockStatus lockStatus;
	private enum LockStatus {
	    LOCKED,
	    UNLOCKED
	}
	private int newFolderNo; // 웹소켓 처리를 위해 추가됨
	 private String code;   // 웹소켓 처리를 위해 추가됨
	private int lockedBy;
	private Timestamp lockTime;
	private int createdBy;
	private Timestamp createdAt;
	private int codeSyncNo;// 웹소켓 처리를 위해 추가됨
	private Date CodeSyncLastUpdate;
	private int userNo; // 웹소켓 처리를 위해 추가됨
	private int isLockedByUser; // 웹소켓 처리를 위해 추가됨
	private String type;// 웹소켓 처리를 위해 추가됨
	private String userId;// 히스토리 처리를 위해 추가됨
	
	@Override
	public String toString() {
	    return "FileVO{" +
	            "fileNo=" + fileNo +
	            ", folderNo=" + folderNo +
	            ", fileName='" + fileName + '\'' +
	            ", extension='" + extension + '\'' +
	            ", content='" + content + '\'' +
	            ", filePath='" + filePath + '\'' +
	            ", lockStatus=" + lockStatus +
	            ", lockedBy=" + lockedBy +
	            ", lockTime=" + lockTime +
	            ", createdBy=" + createdBy +
	            ", createdAt=" + createdAt +
	            ", CodeSyncLastUpdate=" + CodeSyncLastUpdate +
	            '}';
	}
	

}


