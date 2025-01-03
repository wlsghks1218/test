package org.codesync.domain;

import java.sql.Date;

import lombok.Data;

@Data
public class ProjectVO {
	int projectNo, muserNo;
	String projectName, projectDesc, projectDisclosure, token;
	Date projectCreateDate;
	String portfolioLink;
}