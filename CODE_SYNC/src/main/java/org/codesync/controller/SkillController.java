package org.codesync.controller;

import java.util.List;

import org.codesync.domain.SkillVO;
import org.codesync.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j;

@RestController
@Log4j
@RequestMapping("/skill/*")
@CrossOrigin(origins = "*")
public class SkillController {
	
	@Autowired
	private SkillService service;
	
	@GetMapping("/getSkillList")
	public List<SkillVO> getSkillList(@RequestParam("projectNo") int projectNo){
		List<SkillVO> result = service.getSkillList(projectNo);
		return result;
	}
	
    @PostMapping("/insertDefaultSkills")
    public ResponseEntity<String> insertDefaultSkills(@RequestBody List<SkillVO> skills) {
    	service.insertDefaultSkills(skills);
        return ResponseEntity.ok("Default skills inserted successfully.");
    }
    
    @PutMapping("/updateCategory")
    public ResponseEntity<String> updateSkillCategory(@RequestBody SkillVO skill) {
    	service.updateCategory(skill);
        return ResponseEntity.ok("Category updated successfully.");
    }
    
    @PostMapping("/addSkill")
    public ResponseEntity<String> addSkill(@RequestBody SkillVO skill) {
    	service.addSkill(skill);
        return ResponseEntity.ok("Skill added successfully.");
    }
    
    @DeleteMapping("/deleteSkill")
    public ResponseEntity<String> deleteSkill(@RequestBody SkillVO skill) {
    	service.deleteSkill(skill);
        return ResponseEntity.ok("Skill deleted successfully");
    }
	
}
