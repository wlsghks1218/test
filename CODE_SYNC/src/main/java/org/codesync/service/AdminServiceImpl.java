package org.codesync.service;

import java.util.List;
import java.util.UUID;

import org.codesync.domain.ProjectVO;
import org.codesync.domain.UserDTO;
import org.codesync.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService{
	@Autowired
	private AdminMapper mapper;
	
	@Override
	public List<ProjectVO> getProjectList() {
		return mapper.getProjectList();
	}
	
	@Override
	public List<UserDTO> getUserList() {
		return mapper.getUserList();
	}
	
	@Override
	public ProjectVO getProjectDetail(int projectNo) {
		return mapper.getProjectDetail(projectNo);
	}
	
	@Override
	public UserDTO getUserDetail(int userNo) {
		return mapper.getUserDetail(userNo);
	}
	@Override
	public int deleteProject(int projectNo) {
		return mapper.deleteProject(projectNo);
	}
	
	@Override
	public int updateProject(ProjectVO vo) {
		if(vo.getProjectDisclosure().equals("public")) {
			String token = UUID.randomUUID().toString();
			vo.setToken(token);
		}else {
			vo.setToken(null);
		}
		return mapper.updateProject(vo);
	}
	
	@Override
	public int deleteUser(int userNo) {
		return mapper.deleteUser(userNo);
	}
	
	@Override
	public int updateUser(UserDTO vo) {
		return mapper.updateUser(vo);
	}
}
