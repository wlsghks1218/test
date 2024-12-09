package org.codesync.domain;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class DocsVO {
	int docsNo, columnNo, uploadUserNo;
	String docsType, docsName, uploadPath;
	Timestamp uploadTime;
}
