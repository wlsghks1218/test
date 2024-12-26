package org.codesync.controller;

import java.util.List;
import java.util.Map;

import org.codesync.domain.GanttVO;
import org.codesync.service.GanttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j;

@RestController
@Log4j
@RequestMapping("/gantt/*")
@CrossOrigin(origins = "*")
public class GanttController {
	
	@Autowired
	private GanttService service;
	
	@PostMapping("/createGantt")
	public int createGantt(@RequestBody GanttVO vo) {
		int result = service.createGantt(vo);
		return result;
	}
	
    @GetMapping("/{projectNo}")
    public List<GanttVO> getGanttData(@PathVariable int projectNo) {
        return service.getGanttDataByProjectNo(projectNo);
    }
    
    @DeleteMapping("/{ganttNo}")
    public int deleteGantt(@PathVariable int ganttNo) {
       return service.deleteGantt(ganttNo);
    }
    
    @PutMapping("/updateGantt")
    public List<GanttVO> updateGantt(@RequestBody GanttVO vo) {
    	int result = service.updateGantt(vo); 
    	return service.getGanttDataByProjectNo(vo.getProjectNo());
    }
}
