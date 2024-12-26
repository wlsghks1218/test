package org.codesync.domain;

import java.sql.Date;

import lombok.Data;

@Data
public class GanttVO {
	int projectNo, ganttNo;
	String userId, content;
	Date  startDate, endDate;
}
