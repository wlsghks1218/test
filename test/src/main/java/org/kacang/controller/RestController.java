package org.kacang.controller;

import java.util.HashMap;
import java.util.Map;

import org.kacang.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {
	
	@Autowired
	private TestService service;
	
    @GetMapping(value = "/api/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> testApi() {
        int result = service.insertTest(); // 서비스에서 데이터 처리
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("result", result);
        return ResponseEntity.ok(response); // JSON 형태로 반환
    }
    
}
