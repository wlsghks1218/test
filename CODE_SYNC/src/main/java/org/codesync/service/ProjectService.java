package org.codesync.service;

import java.util.List;
import java.util.Map;

import org.codesync.domain.CodeSyncVO;
import org.codesync.domain.DocsWrapperVO;
import org.codesync.domain.ErdVO;
import org.codesync.domain.GanttVO;
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
	public GanttVO getProjectGantt(int projectNo);
	public boolean deleteProject(int projectNo);
	public List<UserDTO> getInvitedUsers(int projectNo);
	public int getProjectNoByToken(String projectToken);
	public int joinProjectByToken(Map<String, Integer> params);
	public int chkProjectJoin(Map<String, Integer> params);
	public int chkProjectExist(String projectToken);
	public int removeUser(Map<String, Integer> params);
	public int cancelInvitation(Map<String, Integer> params);
	public ProjectVO getProjectByProjectNo(int projectNo);
	public int updateProject(ProjectVO vo);
	public int updatePortfolio(Map<String, Object> params);
	public int leaveProject(Map<String, Integer> params);
}
