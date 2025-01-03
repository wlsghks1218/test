package org.codesync.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codesync.domain.CodeSyncVO;
import org.codesync.domain.DocsWrapperVO;
import org.codesync.domain.ErdVO;
import org.codesync.domain.GanttVO;
import org.codesync.domain.ProjectVO;
import org.codesync.domain.UserDTO;
import org.codesync.security.domain.CustomUser;
import org.codesync.service.MemberService;
import org.codesync.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j;

@RestController
@Log4j
@RequestMapping("/project")
@CrossOrigin(origins = "*")
public class ProjectController {
	
	@Autowired
	private ProjectService service;
	
	@Autowired
	private MemberService mservice;
	
    @Autowired
    private JavaMailSender mailSender;
	
	@PostMapping("/createProject")
	public ResponseEntity<String> createProject(@RequestBody ProjectVO pvo){
		int result = service.createProject(pvo);
		log.warn("pvo는 : " + pvo);
		return ResponseEntity.ok("project create success");
	}
	
	@GetMapping("/getProjectList")
	public List<ProjectVO> getProjectList(@RequestParam ("userNo") int userNo) {
		List<ProjectVO> result = service.getProjectList(userNo);
		return result;
	}
	
	@GetMapping("/getProjectUsers")
	public List<UserDTO> getProjectUsers(@RequestParam("projectNo") int projectNo){
		List<UserDTO> result = service.getProjectUsers(projectNo); 
		log.warn(projectNo + "프로젝트의 인원 : " + result);
		return result;
	}
	
    @PostMapping("/inviteUser")
    public ResponseEntity<String> inviteUser(@RequestBody Map<String, String> request) {
        try {
            String projectName = request.get("projectName");
            String userEmail = request.get("userEmail");
            int userNo = Integer.parseInt(request.get("userNo"));
            int projectNo = Integer.parseInt(request.get("projectNo"));
            

            if (userEmail == null || userEmail.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required.");
            }

            // Generate a unique token for the invitation
            String token = UUID.randomUUID().toString();
            Map<String, Object> inviteInfo = new HashMap<>();
            inviteInfo.put("projectName", projectName);
            inviteInfo.put("userEmail", userEmail);
            inviteInfo.put("token", token);
            inviteInfo.put("userNo", userNo);
            inviteInfo.put("projectNo", projectNo);
            int result = service.inviteUser(inviteInfo);

            // Construct the invitation link
            String inviteLink = "http://116.121.53.142:9100/project/acceptInvite?token=" + token;

            // Construct the email content
            String subject = "CODE SYNC: You are invited to join the project '" + projectName + "'";
            String content = "<p>You have been invited to join the project: <strong>" + projectName + "</strong>.</p>"
                    + "<p>To accept the invitation, please click the link below:</p>"
                    + "<a href=\"" + inviteLink + "\">Join the Project</a>";

            // Send the email
            sendInvitationEmail(userEmail, subject, content);

            log.warn("User with email " + userEmail + " has been invited to project " + projectName);
            return ResponseEntity.ok("Invitation sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send invitation.");
        }
    }

    private void sendInvitationEmail(String email, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setFrom("kimkacang@gmail.com", "CODE SYNC"); // 발송자 이메일 + 발송자 이름
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
    
    @GetMapping("/acceptInvite")
    public void acceptInvite(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
    	
        log.warn("acceptInvite token : " + token);
        String reason = service.acceptProjectInvite(token);

        if ("token expired".equals(reason)) {
            response.sendRedirect("http://116.121.53.142:9100/expiredPage");
        } else if ("already joined".equals(reason)) {
            response.sendRedirect("http://116.121.53.142:9100/alreadyJoined");
        } else if ("join success".equals(reason)) {
            response.sendRedirect("http://116.121.53.142:9100/");
        }
    }
    
    @GetMapping("/deleteProject")
    public ResponseEntity<Map<String, Object>> deleteProject(@RequestParam int projectNo) {
        boolean isDeleted = service.deleteProject(projectNo);
        Map<String, Object> response = new HashMap<>();
        response.put("success", isDeleted);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/checkErd")
    public ErdVO checkErd(@RequestParam("projectNo") int projectNo) {
    	ErdVO vo = service.getProjectErd(projectNo);
    	return vo;
    }
    
    @GetMapping("/checkCode")
    public CodeSyncVO checkCode(@RequestParam("projectNo") int projectNo) {
    	CodeSyncVO vo = service.getProjectCode(projectNo);
    	return vo;
    }
    
    @GetMapping("/checkDocs")
    public DocsWrapperVO checkDocs(@RequestParam("projectNo") int projectNo) {
    	DocsWrapperVO vo = service.getProjectDocs(projectNo);
    	return vo;
    }
    
    @GetMapping("/checkGantt")
    public GanttVO checkGantt(@RequestParam("projectNo") int projectNo) {
    	GanttVO vo = service.getProjectGantt(projectNo);
    	return vo;
    }
    
    @GetMapping("/getInvitedUsers")
    public List<UserDTO> getInvitedUsers(@RequestParam("projectNo") int projectNo){
    	List<UserDTO> vo = service.getInvitedUsers(projectNo);
    	return vo;
    }
    
    @GetMapping("/{projectToken}")
    public void joinProjectByToken(@PathVariable String projectToken, HttpSession session, HttpServletResponse response)throws IOException {
    	log.warn("어서오세요");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
        	log.warn("로그인되지 않음");
        	session.setAttribute("token", projectToken);
        	response.sendRedirect("http://116.121.53.142:9100/login");
        	return;
        }
        
        log.warn("로그인되어 있음");
        Object principal = authentication.getPrincipal();
        CustomUser customUser = (CustomUser) principal;
        int userNo = customUser.getUser().getUserNo();
        log.warn("로그인 유저의 userNo: " + userNo);
        int result4 = service.chkProjectExist(projectToken);
        if(result4 == 0) {
        	session.removeAttribute("token");
        	response.sendRedirect("http://116.121.53.142:9100/invalidProject");
        	return;
        }
        
        int result = mservice.getProjectCount(userNo);
        if(result >= 3) {
        	session.removeAttribute("token");
        	response.sendRedirect("http://116.121.53.142:9100/projectLimit");
        	return;
        }
        
        String userId = authentication.getName();
        int projectNo = service.getProjectNoByToken(projectToken);
        
        
        Map<String, Integer> params = new HashMap<>();
        params.put("userNo", userNo);
        params.put("projectNo", projectNo);
        int result3 = service.chkProjectJoin(params);
        if(result3 > 0) {
        	session.removeAttribute("token");
        	response.sendRedirect("http://116.121.53.142:9100/alreadyJoined");
        }else {
        	int result2 = service.joinProjectByToken(params);
        	session.removeAttribute("token");
	    	response.sendRedirect("http://116.121.53.142:9100/");
        }
    }
    
    @PostMapping("/removeUser")
    public int removeUser(@RequestBody Map<String, Integer> params) {
    	int result = service.removeUser(params);
    	return result;
    }
    
    @PostMapping("/cancelInvitation")
    public int cancelInvitation(@RequestBody Map<String, Integer> params) {
    	int result = service.cancelInvitation(params);
    	return result;
    }
    
    @GetMapping("/getProjectByProjectNo")
    public ProjectVO getProjectByProjectNo(@RequestParam("projectNo") int projectNo) {
    	ProjectVO vo = service.getProjectByProjectNo(projectNo);
    	return vo;
    }
    
    @PostMapping("/updateProject")
    public int updateProject(@RequestBody ProjectVO vo) {
    	int result = service.updateProject(vo);
    	return result;
    }
    
    @PostMapping("/updatePortfolio")
    public int updatePortfolio(@RequestBody Map<String, Object> params) {
    	int result = service.updatePortfolio(params);
    	return result;
    }
}
