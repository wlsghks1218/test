package org.codesync.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codesync.domain.CodeSyncVO;
import org.codesync.domain.DocsWrapperVO;
import org.codesync.domain.ErdVO;
import org.codesync.domain.ProjectInviteVO;
import org.codesync.domain.ProjectUserVO;
import org.codesync.domain.ProjectVO;
import org.codesync.domain.UserDTO;
import org.codesync.mapper.MemberMapper;
import org.codesync.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class ProjectServiceImpl implements ProjectService{
	
	@Autowired
	private ProjectMapper mapper;
	
	@Autowired
	private MemberMapper mmapper;
	
	@Override
	@Transactional
	public int createProject(ProjectVO pvo) {
		log.warn("public 여부 : " + pvo.getProjectDisclosure());
		if(pvo.getProjectDisclosure().equals("public")) {
			String token = UUID.randomUUID().toString();
			pvo.setToken(token);
		}
	    int result = mapper.createProject(pvo);
	    if (result > 0) {
	        Map<String, Integer> map = new HashMap<>();
	        map.put("userNo", pvo.getMuserNo());
	        map.put("projectNo", pvo.getProjectNo());
	        mapper.createErdSync(pvo.getProjectNo());
	        mapper.createCodeSync(pvo.getProjectNo());
	        mapper.createDocsWrapper(pvo.getProjectNo());
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
	
	@Override
	public ErdVO getProjectErd(int projectNo) {
		return mapper.getProjectErd(projectNo);
	}
	
	@Override
	public CodeSyncVO getProjectCode(int projectNo) {
		return mapper.getProjectCode(projectNo);
	}
	
	@Override
	public DocsWrapperVO getProjectDocs(int projectNo) {
		return mapper.getProjectDocs(projectNo);
	}
	
	@Override
	public boolean deleteProject(int projectNo) {
		int result = mapper.deleteProject(projectNo); 
		return result>0;
	}
	
	@Override
	public List<UserDTO> getInvitedUsers(int projectNo) {
	    List<Integer> userNoList = mapper.getInvitedUserNo(projectNo);
	    
	    if (userNoList == null || userNoList.isEmpty()) {
	        return Collections.emptyList();
	    }
	    
	    List<UserDTO> invitedUserList = new ArrayList<>();
	    
	    for (int userNo : userNoList) {
	        UserDTO userInfo = mapper.getInvitedUserInfo(userNo);
	        if (userInfo != null) {
	            invitedUserList.add(userInfo);
	        }
	    }
	    return invitedUserList;
	}

	@Override
	public int getProjectNoByToken(String projectToken) {
		return mapper.getProjectNoByToken(projectToken);
	}
	
	@Override
	public int joinProjectByToken(Map<String, Integer> params) {
		return mapper.joinProjectByToken(params);
	}
	
	@Override
	public int chkProjectJoin(Map<String, Integer> params) {
		return mapper.chkProjectJoin(params);
	}
	
	@Override
	public int chkProjectExist(String projectToken) {
		return mapper.chkProjectExist(projectToken);
	}
	
	@Override
	public int removeUser(Map<String, Integer> params) {
		return mapper.removeUser(params);
	}
	
	@Override
	public int cancelInvitation(Map<String, Integer> params) {
		return mapper.cancelInvitation(params);
	}
	
	@Override
	public ProjectVO getProjectByProjectNo(int projectNo) {
		return mapper.getProjectByProjectNo(projectNo);
	}
	
}
