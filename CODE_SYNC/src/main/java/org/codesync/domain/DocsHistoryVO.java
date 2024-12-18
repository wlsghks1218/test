package org.codesync.domain;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class DocsHistoryVO {
	int projectNo, action, columnIndex;
	String userId, fileName, columnName; 
	Timestamp updateDate;
}
