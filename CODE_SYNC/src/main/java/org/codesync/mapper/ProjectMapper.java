package org.codesync.mapper;

import java.util.List;
import java.util.Map;

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
}
