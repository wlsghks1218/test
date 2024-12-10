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
	private int fileNo;
	private int folderNo;
	private String fileName;
	private String extension;
	private String content;
	private String filePath;
	private LockStatus lockStatus;
	private enum LockStatus {
	    LOCKED,
	    UNLOCKED
	}
	private int lockedBy;
	private Timestamp lockTime;
	private int createdBy;
	private Timestamp createdAt;
	private int codeSyncNo;
	private Date gitLastUpdate;
	private int gitNo;
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
	            ", gitLastUpdate=" + gitLastUpdate +
	            '}';
	}

}


