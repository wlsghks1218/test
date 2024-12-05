package org.codesync.service;

import java.util.List;
import java.util.Map;

import org.codesync.domain.ProjectUserVO;
import org.codesync.domain.ProjectVO;
import org.codesync.domain.UserDTO;

public interface ProjectService {
	public int createProject(ProjectVO pvo);
	public List<ProjectVO> getProjectList(int userNo);
	public List<UserDTO> getProjectUsers(int projectNo);
	public int inviteUser(Map<String, Object> inviteInfo);
	public String acceptProjectInvite(String token);
}
