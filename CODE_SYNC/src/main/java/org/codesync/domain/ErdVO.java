package org.codesync.domain;

import java.sql.Date;

import lombok.Data;

@Data
public class ErdVO {
	int erdNo, projectNo;
	Date erdCreateDate;
}
