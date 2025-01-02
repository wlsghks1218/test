package org.codesync.controller;

import java.util.List;

import org.codesync.domain.ProjectVO;
import org.codesync.domain.UserDTO;
import org.codesync.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j;

@RestController
@RequestMapping("/admin/*")
@CrossOrigin(origins="*")
@Log4j
public class AdminController {
	
	@Autowired
	private AdminService service;
	
	@GetMapping("/getProjectList")
	public List<ProjectVO> getProjectList(){
		List<ProjectVO> list = service.getProjectList();
		return list; 
	}
	
	@GetMapping("/getUserList")
	public List<UserDTO> getUserList(){
		List<UserDTO> list = service.getUserList();
		return list; 
	}
	
	@GetMapping("/project/{id}")
	public ProjectVO getProjectDetail(@PathVariable("id") int projectNo) {
		ProjectVO vo = service.getProjectDetail(projectNo);
		return vo;
	}
	
	@GetMapping("/user/{id}")
	public UserDTO getUserDetail(@PathVariable("id") int userNo) {
		UserDTO vo = service.getUserDetail(userNo);
		return vo;
	}
	
	@PutMapping("/updateProject")
	public int updateProject(@RequestBody ProjectVO vo) {
		int result = service.updateProject(vo);
		return result;
	}
	
	@DeleteMapping("/deleteProject/{id}")
	public int deleteProject(@PathVariable("id") int projectNo) {
		int result = service.deleteProject(projectNo);
		return result;
	}
	
	@PutMapping("/updateUser")
	public int updateUser(@RequestBody UserDTO vo) {
		int result = service.updateUser(vo);
		return result;
	}
	
	@DeleteMapping("/deleteUser/{id}")
	public int deleteUser(@PathVariable("id") int userNo) {
		int result = service.deleteUser(userNo);
		return result;
	}
}