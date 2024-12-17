package org.codesync.service;

import java.util.List;
import java.util.Map;

import org.codesync.domain.CodeSyncVO;
import org.codesync.domain.DocsWrapperVO;
import org.codesync.domain.ErdVO;
import org.codesync.domain.ProjectUserVO;
import org.codesync.domain.ProjectVO;
import org.codesync.domain.UserDTO;

public interface ProjectService {
	public int createProject(ProjectVO pvo);
	public List<ProjectVO> getProjectList(int userNo);
	public List<UserDTO> getProjectUsers(int projectNo);
	public int inviteUser(Map<String, Object> inviteInfo);
	public String acceptProjectInvite(String token);
	public ErdVO getProjectErd(int projectNo);
	public CodeSyncVO getProjectCode(int projectNo);
	public DocsWrapperVO getProjectDocs(int projectNo);
	public boolean deleteProject(int projectNo);
	public List<UserDTO> getInvitedUsers(int projectNo);
}
