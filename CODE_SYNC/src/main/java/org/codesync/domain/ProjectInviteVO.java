package org.codesync.domain;

import lombok.Data;

@Data
public class ProjectInviteVO {
	String userEmail, token, projectName;
	int projectNo, userNo;
}
