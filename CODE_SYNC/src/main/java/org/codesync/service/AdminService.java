package org.codesync.service;

import java.util.List;

import org.codesync.domain.ProjectVO;
import org.codesync.domain.UserDTO;

public interface AdminService {
	public List<ProjectVO> getProjectList();
	public List<UserDTO> getUserList();
	public ProjectVO getProjectDetail(int projectNo);
	public UserDTO getUserDetail(int userNo);
	public int updateProject(ProjectVO vo);
	public int deleteProject(int projectNo);
	public int updateUser(UserDTO vo);
	public int deleteUser(int userNo);
}
