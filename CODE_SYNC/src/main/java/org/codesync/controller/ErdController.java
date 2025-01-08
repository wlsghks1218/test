package org.codesync.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.codesync.domain.ArrowVO;
import org.codesync.domain.ChatContentVO;
import org.codesync.domain.ErdHistoryVO;
import org.codesync.domain.MemoVO;
import org.codesync.domain.ProjectVO;
import org.codesync.domain.TableFieldsVO;
import org.codesync.domain.TableVO;
import org.codesync.service.ErdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j;

@CrossOrigin(origins = "*")  
@RestController
@Log4j
@RequestMapping("/erd")
public class ErdController {

    @Autowired
    private ErdService service;

    @GetMapping("/userId")
    public ResponseEntity<Map<String, String>> getUserId(@RequestParam("userNo") String userNo) {
        String userId = service.getUserId(userNo);

        Map<String, String> response = new HashMap<>();
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/chatHistory")
    public ResponseEntity<List<ChatContentVO>> getChatHistory(@RequestParam String erdNo) {
        try {
            List<ChatContentVO> chatHistory = service.getChatHistory(erdNo); // 채팅 기록 가져오기
            return ResponseEntity.ok(chatHistory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/tables")
    public ResponseEntity<List<TableVO>> getTables(@RequestParam String erdNo) {
        try {
            List<TableVO> tables = service.getTables(erdNo); // 채팅 기록 가져오기
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/memos")
    public ResponseEntity<List<MemoVO>> getMemos(@RequestParam String erdNo) {
    	try {
    		List<MemoVO> memos = service.getMemos(erdNo); 
    		return ResponseEntity.ok(memos);
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    	}
    }
    
    @GetMapping("/arrows")
    public ResponseEntity<List<ArrowVO>> getArrows(@RequestParam String erdNo) {
    	try {
    		List<ArrowVO> arrows = service.getArrows(erdNo); 
    		return ResponseEntity.ok(arrows);
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    	}
    }
    
    @GetMapping("/tableFields")
    public ResponseEntity<List<TableFieldsVO>> getTableFields(@RequestParam String id) {
    	
    	System.out.println("받은 아이디 " + id);
        try {
            List<TableFieldsVO> fields = service.getTableFields(id);
            return ResponseEntity.ok(fields);
        } catch (Exception e) {
            e.printStackTrace();  
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 상태 반환
        }
    }
    
    @PostMapping("/addHistory")
    public ResponseEntity<String> addHistory(@RequestBody ErdHistoryVO history) {
        try {
            service.addHistory(history); 
            return ResponseEntity.ok("History added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add history");
        }
    }
    
    @GetMapping("/history/{erdNo}")
    public ResponseEntity<List<ErdHistoryVO>> getHistories(@PathVariable String erdNo) {
        try {
            // erdNo를 이용해 특정 히스토리를 가져오는 로직
        	List<ErdHistoryVO> histories = service.getHistories();
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping("/getProject")
    public ResponseEntity<Object> getProject(@RequestBody Map<String, Object> request) {
        try {
            // Request에서 codeSyncNo를 String으로 받고 Integer로 변환
            String erdNoStr = (String) request.get("erdNo");
            int erdNo = Integer.parseInt(erdNoStr);

            // 서비스 호출로 프로젝트 정보 가져오기
            ProjectVO result = service.getProject(erdNo);

            // 결과를 ResponseEntity로 반환
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NumberFormatException e) {
            log.error("Invalid codeSyncNo format", e);
            return new ResponseEntity<>("Invalid codeSyncNo format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error retrieving project information", e);
            return new ResponseEntity<>("Failed to retrieve project information", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
