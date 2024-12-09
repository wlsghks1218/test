package org.codesync.domain;

import java.util.List;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class DocsWrapperVO {
	int projectNo, wrapperNo;
	Timestamp createdDate;
	
	List<DocsColumnVO> columnList;
}
