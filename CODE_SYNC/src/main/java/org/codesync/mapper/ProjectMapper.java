package org.codesync.mapper;

import java.util.List;
import java.util.Map;

import org.codesync.domain.CodeSyncVO;
import org.codesync.domain.DocsWrapperVO;
import org.codesync.domain.ErdVO;
import org.codesync.domain.GanttVO;
import org.codesync.domain.ProjectInviteVO;
import org.codesync.domain.ProjectUserVO;
import org.codesync.domain.ProjectVO;
import org.codesync.domain.UserDTO;

public interface ProjectMapper {
	public int createProject(ProjectVO pvo);
	public int insertProjectMasterUser(Map<String, Integer> map);
	public List<ProjectUserVO> getProjectList(int userNo);
	public ProjectVO getProjectByProjectNo(int projectNo);
	public List<ProjectUserVO> getProjectUsers(int projectNo);
	public UserDTO getProjectUserInfo(int userNo);
	public int inviteUser(Map<String, Object> inviteInfo);
	public int chkInvitedDuplicated(Map<String, Object> inviteInfo);
	public int deleteInviteInfo(Map<String, Object> inviteInfo);
	public ProjectInviteVO getInviteInfo(String token);
	public int insertProjectMember(Map<String, Integer> map);
	public int deleteToken(String token);
	public int chkExpired(String token);
	public ErdVO getProjectErd(int projectNo);
	public CodeSyncVO getProjectCode(int projectNo);
	public DocsWrapperVO getProjectDocs(int projectNo);
	public GanttVO getProjectGantt(int projectNo);
	public int createErdSync(int projectNo);
	public int createCodeSync(int projectNo);
	public int createDocsWrapper(int projectNo);
	public int deleteProject(int projectNo);
	public List<Integer> getInvitedUserNo(int projectNo);
	public UserDTO getInvitedUserInfo(int userNo);
	public int getProjectNoByToken(String projectToken);
	public int joinProjectByToken(Map<String, Integer> params);
	public int chkProjectJoin(Map<String, Integer> params);
	public int chkProjectExist(String projectToken);
	public int removeUser(Map<String, Integer> params);
	public int cancelInvitation(Map<String, Integer> params);
	public int updateProject(ProjectVO vo);
	public int updatePortfolio(Map<String, Object> params);
	public int leaveProject(Map<String, Integer> params);
}
