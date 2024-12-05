package org.codesync.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codesync.domain.ProjectInviteVO;
import org.codesync.domain.ProjectUserVO;
import org.codesync.domain.ProjectVO;
import org.codesync.domain.UserDTO;
import org.codesync.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class ProjectServiceImpl implements ProjectService{
	
	@Autowired
	private ProjectMapper mapper;
	
	@Override
	public int createProject(ProjectVO pvo) {
	    int result = mapper.createProject(pvo);
	    if (result > 0) {
	        Map<String, Integer> map = new HashMap<>();
	        map.put("userNo", pvo.getMuserNo());
	        map.put("projectNo", pvo.getProjectNo());
	        return mapper.insertProjectMasterUser(map);
	    }
	    return 0;
	}
	
	@Override
	public List<ProjectVO> getProjectList(int userNo) {
	    List<ProjectUserVO> projectListByUserNo = mapper.getProjectList(userNo);

	    List<ProjectVO> pvoList = new ArrayList<>();

	    for (ProjectUserVO vo : projectListByUserNo) {
	        ProjectVO project = mapper.getProjectByProjectNo(vo.getProjectNo());
	        if (project != null) {
	            pvoList.add(project);
	        }
	    }

	    return pvoList;
	}
	
	@Override
	public List<UserDTO> getProjectUsers(int projectNo) {
		List<ProjectUserVO> result = mapper.getProjectUsers(projectNo);
		List<UserDTO> userList = new ArrayList<>();
		for(ProjectUserVO vo : result) {
			UserDTO user = mapper.getProjectUserInfo(vo.getUserNo());
			if (user != null) {
				userList.add(user);
			}
		}
		return userList;
	}
	
	@Override
	public int inviteUser(Map<String, Object> inviteInfo) {
		int result = mapper.chkInvitedDuplicated(inviteInfo);
		log.warn("중복되는 초대 개수 : " + result);
	    if (result > 0) {
	        mapper.deleteInviteInfo(inviteInfo);
	    }
		return mapper.inviteUser(inviteInfo);
	}
	
	@Override
	public String acceptProjectInvite(String token) {
		int chkExpired = mapper.chkExpired(token);
		if(chkExpired == 0) {
			return "token expired";
		}
		ProjectInviteVO vo = mapper.getInviteInfo(token);
        Map<String, Integer> map = new HashMap<>();
        map.put("userNo", vo.getUserNo());
        map.put("projectNo", vo.getProjectNo());
        int result = mapper.insertProjectMember(map);
        int result2 = mapper.deleteToken(token);
        return "join success";
	}
}
