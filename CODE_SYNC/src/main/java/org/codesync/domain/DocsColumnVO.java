package org.codesync.domain;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

@Data
public class DocsColumnVO {
	int columnNo, wrapperNo, columnCreator;
	String columnName;
	Timestamp columnCreatedDate;
	List<DocsVO> voList;
}
