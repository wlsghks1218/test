package org.codesync.domain;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class ErdHistoryVO {
	private int erdHistoryNo;
	private String action;
	private Timestamp erdUpdateDate;
	
}

