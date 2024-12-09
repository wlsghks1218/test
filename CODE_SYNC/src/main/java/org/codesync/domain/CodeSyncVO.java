package org.codesync.domain;

import java.sql.Date;

import lombok.Data;

@Data
public class CodeSyncVO {
	int codeSyncNo, projectNo;
	Date codeSyncCreatedDate;
}
