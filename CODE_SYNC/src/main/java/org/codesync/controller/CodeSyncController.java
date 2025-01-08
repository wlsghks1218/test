package org.codesync.controller;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codesync.domain.ChatContentVO;
import org.codesync.domain.CodeSyncChatContentVO;
import org.codesync.domain.CodeSyncHistoryVO;
import org.codesync.domain.FileVO;
import org.codesync.domain.FolderStructureVO;
import org.codesync.domain.FolderVO;
import org.codesync.domain.ProjectVO;
import org.codesync.service.CodeSyncChatService;
import org.codesync.service.CodeSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/codeSync")
public class CodeSyncController {

    @Autowired
    private CodeSyncService service;
   
    @Autowired
    private CodeSyncChatService chatService;

    @PostMapping(value = "/uploadFolder", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> uploadFolder(@RequestBody FolderStructureVO folderStructure) {

        try {
            // 1. 입력 데이터 검증
            if (folderStructure == null || 
                folderStructure.getFolders() == null || 
                folderStructure.getFiles() == null) {
                log.warn("Invalid folder structure data received");
                return new ResponseEntity<>("Invalid folder structure data", HttpStatus.BAD_REQUEST);
            }

            log.debug("폴더 및 파일 구조 처리 시작");

            // 2. 폴더 데이터 저장 (파일 경로 저장)
            folderStructure.getFolders().forEach(folder -> {
                try {
                	System.out.println("넘어온 넘버" + folder.getCodeSyncNo());
                    // 폴더 데이터 저장 후 filePath 반환
                    String filePath = service.saveFolderData(folder);
                    log.info("Folder saved: " + folder.getFolderName());

                    // 폴더 저장 후 filePath를 기반으로 부모 폴더 ID를 찾는 작업은 나중에 처리
                } catch (Exception ex) {
                    log.error("Error saving folder: " + folder.getFolderName(), ex);
                    throw new RuntimeException("Failed to save folder: " + folder.getFolderName());
                }
            });

            // 3. 부모 폴더 ID 업데이트 (파일 경로 기반)
            folderStructure.getFolders().forEach(folder -> {
                try {
                    // 저장된 폴더의 filePath를 기반으로 부모 폴더 fileNo 찾기
                    String filePath = folder.getFolderPath(); // filePath는 saveFolderData에서 저장됨
                    int codeSyncNo = folder.getCodeSyncNo();
                    System.out.println("코드 싱크 넘버는 ? " + codeSyncNo);
                    Integer parentFolderFileNo = service.findParentFolderFileNo(filePath , codeSyncNo);
                    System.out.println("부모 넘버는 " + parentFolderFileNo);
                    System.out.println("자식 넘버는 " + folder.getFolderNo());
                    Integer folderNo = service.getFileNo(folder.getFolderPath() , codeSyncNo);

                    if (parentFolderFileNo != null) {
                        // 부모 폴더 fileNo로 자식 폴더의 부모 ID 업데이트
                        service.updateFolderParentId(folderNo, parentFolderFileNo);
                        log.info("Parent folder ID updated for folder: " + folder.getFolderName());
                    }
                } catch (Exception ex) {
                    log.error("Error updating parent folder ID: " + folder.getFolderName(), ex);
                    throw new RuntimeException("Failed to update parent folder ID for: " + folder.getFolderName());
                }
            });

         // 4. 파일 데이터 처리
            folderStructure.getFiles().forEach(file -> {
                try {
                    log.info("Processing file: " + file.getFileName());
                    System.out.println("확장자는?" + file.getExtension());

                    // 파일의 확장자가 .map인지 체크
                    if (file.getFileName().endsWith(".map")) {
                        String errorMessage = "Source map files cannot be saved: " + file.getFileName();
                        log.error(errorMessage);  // 로깅
                        // .map 파일은 저장하지 않고 넘어감
                        return;
                    }

                    int codeSyncNo = file.getCodeSyncNo();
                    int folderNo = service.getFolderNo(file.getFilePath(), codeSyncNo);
                    file.setFolderNo(folderNo);

                    // 단일 FileVO 객체를 서비스로 전달
                    service.saveFile(file);  // 파일 저장

                    log.info("File saved successfully: " + file.getFileName());
                } catch (Exception ex) {
                    log.error("Error saving file: " + file.getFileName(), ex);
                    // 예외 던지지 않고 계속 진행
                    log.warn("Skipping file due to error: " + file.getFileName());
                }
            });
            log.debug("폴더 및 파일 구조 처리 완료");
            return new ResponseEntity<>("Folder structure uploaded successfully!", HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error uploading folder structure", e);
            return new ResponseEntity<>("Failed to upload folder structure", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping(value = "/folderStructure")
    public ResponseEntity<FolderStructureVO> getFolderStructure(@RequestParam int codeSyncNo) {
        try {
            // codeSyncNo에 해당하는 폴더 구조를 DB에서 조회
            FolderStructureVO folderStructure = service.getFolderStructureByCodeSyncNo(codeSyncNo);

            if (folderStructure != null && 
                (folderStructure.getFolders() != null && !folderStructure.getFolders().isEmpty()) || 
                (folderStructure.getFiles() != null && !folderStructure.getFiles().isEmpty())) {
                // 폴더나 파일이 하나라도 있으면 200 OK 반환
                return ResponseEntity.ok(folderStructure);
            } else {
                // 데이터가 없으면 404 반환
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            log.error("Error fetching folder structure for codeSyncNo: " + codeSyncNo, e);
            // 서버 에러 발생 시 500 반환
            return ResponseEntity.status(500).body(null);
        }
    }
    @PostMapping("/getFileNo")
    public Integer getFileNo(@RequestBody FileVO request) {
        // folderNo와 fileName을 사용하여 fileNo 조회
        return service.getFileNoByFolderAndFileName(request.getFolderNo(), request.getFileName());
    }
    @PostMapping("/saveCode")
    public ResponseEntity<String> saveCode(@RequestBody FileVO request) {
        try {
            // fileNo와 content가 비어있는지 확인
            if (request.getFileNo() == 0 || request.getContent() == null || request.getContent().isEmpty()) {
                log.warn("Invalid data received for saveCode");
                return new ResponseEntity<>("Invalid fileNo or content", HttpStatus.BAD_REQUEST);
            }

            // 파일 저장 로직 (예: 파일 내용을 DB에 저장)
            boolean isSaved = service.saveCode(request.getFileNo(), request.getContent());

            if (isSaved) {
            	int codeSyncNo = request.getCodeSyncNo();
            	int projectNo = service.getProjectNoByCodeSyncNo(codeSyncNo);
            	String fileName = service.getFileNameByFileNo(request.getFileNo());
            	String userId = request.getUserId();
            	
            	CodeSyncHistoryVO hvo = new CodeSyncHistoryVO();
            	hvo.setAction(1);
            	hvo.setFileName(fileName);
            	hvo.setProjectNo(projectNo);
            	hvo.setUserId(userId);
            	service.insertSaveCodeHis(hvo);
            	
                return new ResponseEntity<>("Code saved successfully!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to save code", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            

        } catch (Exception e) {
            log.error("Error saving code", e);
            return new ResponseEntity<>("Failed to save code", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // 파일 잠금 상태 확인
    @PostMapping("/checkFileLockStatus")
    public ResponseEntity<Map<String, Object>> checkFileLockStatus(@RequestBody Map<String, Object> request) {
        int fileNo = (int) request.get("fileNo");
        int userNo = (int) request.get("userNo");
        // DB에서 해당 파일의 잠금 상태 확인
        boolean isLockedByAnotherUser = service.isFileLockedByAnotherUser(fileNo, userNo);

        // 응답에 포함할 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("isLockedByAnotherUser", isLockedByAnotherUser);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/checkLocked")
    public ResponseEntity<Map<String, Object>> checkLocked(@RequestBody Map<String, Object> request) {
        int fileNo = (int) request.get("fileNo");
        int userNo = (int) request.get("userNo");

        // 서비스에서 잠금 상태를 확인
        boolean isLocked = service.checkFileLockStatus(fileNo, userNo);

        // 응답 데이터 준비
        Map<String, Object> response = new HashMap<>();
        response.put("isLocked", isLocked);  // 잠금 상태를 true/false로 반환

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/checkWhoLocked")
    public ResponseEntity<Map<String, Object>> checkWhoLocked(@RequestBody Map<String, Object> request) {
        int fileNo = (int) request.get("fileNo");
        int userNo = (int) request.get("userNo");

        int isLockedByUser = service.getLockedFile(fileNo, userNo);

        

        // 응답 데이터 준비
        Map<String, Object> response = new HashMap<>();
        response.put("isLocked", isLockedByUser);  // 잠금 상태를 true/false로 반환

        return ResponseEntity.ok(response);
    }
    @GetMapping("/chatHistory")
    public ResponseEntity<List<CodeSyncChatContentVO>> getChatHistory(@RequestParam String codeSyncNo) {
        try {
            List<CodeSyncChatContentVO> chatHistory = chatService.getChatHistory(codeSyncNo); // 채팅 기록 가져오기
            return ResponseEntity.ok(chatHistory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/history/{codeSyncNo}")
    public ResponseEntity<List<CodeSyncHistoryVO>> getHistory(@PathVariable int codeSyncNo) {
        try {
            // CodeSyncNo에 맞는 히스토리 데이터를 가져옵니다.
        	int projectNo = service.getProjectNoByCodeSyncNo(codeSyncNo);
            List<CodeSyncHistoryVO> history = service.getHistoryByProjectNo(projectNo); 
            if (history != null && !history.isEmpty()) {
                return ResponseEntity.ok(history);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
        } catch (Exception e) {
            log.error("Error fetching history for codeSyncNo: " + codeSyncNo, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping("/insertCreateFileHistory")
    public ResponseEntity<String> insertCreateFileHistory(@RequestBody Map<String, Object> request){
        try {
            // request에서 필요한 값 추출
            String newName = (String) request.get("newName");
            String codeSyncNoStr = (String) request.get("codeSyncNo");  // String으로 받기
            int codeSyncNo = Integer.parseInt(codeSyncNoStr);  // Integer로 변환
            String userId = (String) request.get("userId");

            // 파일 생성 히스토리 저장
            CodeSyncHistoryVO history = new CodeSyncHistoryVO();
            history.setFileName(newName);
            history.setProjectNo(service.getProjectNoByCodeSyncNo(codeSyncNo));  // 프로젝트 번호 조회
            history.setUserId(userId);
            history.setAction(2);

            // 생성된 파일 히스토리 저장
            service.insertSaveCodeHis(history);

            return new ResponseEntity<>("File creation history recorded successfully!", HttpStatus.OK);
        } catch (NumberFormatException e) {
            log.error("Invalid codeSyncNo format", e);
            return new ResponseEntity<>("Invalid codeSyncNo format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error inserting file creation history", e);
            return new ResponseEntity<>("Failed to record file creation history", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/insertCreateFolderHistory")
    public ResponseEntity<String> insertCreateFolderHistory(@RequestBody Map<String, Object> request){
        try {
            // request에서 필요한 값 추출
            String newName = (String) request.get("newName");
            String codeSyncNoStr = (String) request.get("codeSyncNo");  // String으로 받기
            int codeSyncNo = Integer.parseInt(codeSyncNoStr);  // Integer로 변환
            String userId = (String) request.get("userId");

            // 파일 생성 히스토리 저장
            CodeSyncHistoryVO history = new CodeSyncHistoryVO();
            history.setFolderName(newName);
            history.setProjectNo(service.getProjectNoByCodeSyncNo(codeSyncNo));  // 프로젝트 번호 조회
            history.setUserId(userId);
            history.setAction(2);

            // 생성된 파일 히스토리 저장
            service.insertCreateFolderHis(history);

            return new ResponseEntity<>("File creation history recorded successfully!", HttpStatus.OK);
        } catch (NumberFormatException e) {
            log.error("Invalid codeSyncNo format", e);
            return new ResponseEntity<>("Invalid codeSyncNo format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error inserting file creation history", e);
            return new ResponseEntity<>("Failed to record file creation history", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/insertDeleteFileHistory")
    public ResponseEntity<String> insertDeleteFileHistory(@RequestBody Map<String, Object> request){
        try {
            // request에서 필요한 값 추출
            String fileName = (String) request.get("fileName");
            String codeSyncNoStr = (String) request.get("codeSyncNo");  // String으로 받기
            int codeSyncNo = Integer.parseInt(codeSyncNoStr);  // Integer로 변환
            String userId = (String) request.get("userId");
            

            // 파일 생성 히스토리 저장
            CodeSyncHistoryVO history = new CodeSyncHistoryVO();
            history.setFileName(fileName);
            history.setProjectNo(service.getProjectNoByCodeSyncNo(codeSyncNo));  // 프로젝트 번호 조회
            history.setUserId(userId);
            history.setAction(3);

            // 생성된 파일 히스토리 저장
            service.insertSaveCodeHis(history);

            return new ResponseEntity<>("File creation history recorded successfully!", HttpStatus.OK);
        } catch (NumberFormatException e) {
            log.error("Invalid codeSyncNo format", e);
            return new ResponseEntity<>("Invalid codeSyncNo format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error inserting file creation history", e);
            return new ResponseEntity<>("Failed to record file creation history", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/insertDeleteFolderHistory")
    public ResponseEntity<String> insertDeleteFolderHistory(@RequestBody Map<String, Object> request){
        try {
            // request에서 필요한 값 추출
            String folderName = (String) request.get("folderName");
            String codeSyncNoStr = (String) request.get("codeSyncNo");  // String으로 받기
            int codeSyncNo = Integer.parseInt(codeSyncNoStr);  // Integer로 변환
            String userId = (String) request.get("userId");
            


            // 파일 생성 히스토리 저장
            CodeSyncHistoryVO history = new CodeSyncHistoryVO();
            history.setFolderName(folderName);
            history.setProjectNo(service.getProjectNoByCodeSyncNo(codeSyncNo));  // 프로젝트 번호 조회
            history.setUserId(userId);
            history.setAction(3);

            // 생성된 파일 히스토리 저장
            service.insertCreateFolderHis(history);

            return new ResponseEntity<>("File creation history recorded successfully!", HttpStatus.OK);
        } catch (NumberFormatException e) {
            log.error("Invalid codeSyncNo format", e);
            return new ResponseEntity<>("Invalid codeSyncNo format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error inserting file creation history", e);
            return new ResponseEntity<>("Failed to record file creation history", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/insertPasteFolderHistory")
    public ResponseEntity<String> insertPasteFolderHistory(@RequestBody Map<String, Object> request){
        try {
            // request에서 필요한 값 추출
            String folderName = (String) request.get("folderName");
            String newName = (String) request.get("newName");
            
            String codeSyncNoStr = (String) request.get("codeSyncNo");  // String으로 받기
            int codeSyncNo = Integer.parseInt(codeSyncNoStr);  // Integer로 변환
            String userId = (String) request.get("userId");
            
            System.out.println(newName);
            


            // 파일 생성 히스토리 저장
            CodeSyncHistoryVO history = new CodeSyncHistoryVO();
            history.setFolderName(folderName);
            history.setNewFolderName(newName);
            history.setProjectNo(service.getProjectNoByCodeSyncNo(codeSyncNo));  // 프로젝트 번호 조회
            history.setUserId(userId);
            history.setAction(4);

            // 생성된 파일 히스토리 저장
            service.insertPasteFolderHistory(history);

            return new ResponseEntity<>("File creation history recorded successfully!", HttpStatus.OK);
        } catch (NumberFormatException e) {
            log.error("Invalid codeSyncNo format", e);
            return new ResponseEntity<>("Invalid codeSyncNo format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error inserting file creation history", e);
            return new ResponseEntity<>("Failed to record file creation history", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/insertPasteFileHistory")
    public ResponseEntity<String> insertPasteFileHistory(@RequestBody Map<String, Object> request){
        try {
            // request에서 필요한 값 추출
            String fileName = (String) request.get("fileName");
            String newName = (String) request.get("newName");
            
            String codeSyncNoStr = (String) request.get("codeSyncNo");  // String으로 받기
            int codeSyncNo = Integer.parseInt(codeSyncNoStr);  // Integer로 변환
            String userId = (String) request.get("userId");
            
            System.out.println(newName);
            


            // 파일 생성 히스토리 저장
            CodeSyncHistoryVO history = new CodeSyncHistoryVO();
            history.setFileName(fileName);
            history.setNewFolderName(newName);
            history.setProjectNo(service.getProjectNoByCodeSyncNo(codeSyncNo));  // 프로젝트 번호 조회
            history.setUserId(userId);
            history.setAction(4);

            // 생성된 파일 히스토리 저장
            service.insertPasteFileHistory(history);

            return new ResponseEntity<>("File creation history recorded successfully!", HttpStatus.OK);
        } catch (NumberFormatException e) {
            log.error("Invalid codeSyncNo format", e);
            return new ResponseEntity<>("Invalid codeSyncNo format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error inserting file creation history", e);
            return new ResponseEntity<>("Failed to record file creation history", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/insertRenameFolderHistory")
    public ResponseEntity<String> insertRenameFolderHistory(@RequestBody Map<String, Object> request){
        try {
            // request에서 필요한 값 추출
            String folderName = (String) request.get("folderName");
            String newName = (String) request.get("newName");
            
            String codeSyncNoStr = (String) request.get("codeSyncNo");  // String으로 받기
            int codeSyncNo = Integer.parseInt(codeSyncNoStr);  // Integer로 변환
            String userId = (String) request.get("userId");
            
            


            // 파일 생성 히스토리 저장
            CodeSyncHistoryVO history = new CodeSyncHistoryVO();
            history.setFolderName(folderName);
            history.setNewFolderName(newName);
            history.setProjectNo(service.getProjectNoByCodeSyncNo(codeSyncNo));  // 프로젝트 번호 조회
            history.setUserId(userId);
            history.setAction(5);

            // 생성된 파일 히스토리 저장
            service.insertRenameFolderHistory(history);

            return new ResponseEntity<>("File creation history recorded successfully!", HttpStatus.OK);
        } catch (NumberFormatException e) {
            log.error("Invalid codeSyncNo format", e);
            return new ResponseEntity<>("Invalid codeSyncNo format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error inserting file creation history", e);
            return new ResponseEntity<>("Failed to record file creation history", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } 
    @PostMapping("/insertRenameFileHistory")
    public ResponseEntity<String> insertRenameFileHistory(@RequestBody Map<String, Object> request){
        try {
            // request에서 필요한 값 추출
            String fileName = (String) request.get("fileName");
            String newName = (String) request.get("newName");
            
            String codeSyncNoStr = (String) request.get("codeSyncNo");  // String으로 받기
            int codeSyncNo = Integer.parseInt(codeSyncNoStr);  // Integer로 변환
            String userId = (String) request.get("userId");
            
            


            // 파일 생성 히스토리 저장
            CodeSyncHistoryVO history = new CodeSyncHistoryVO();
            history.setFileName(fileName);
            history.setNewName(newName);
            history.setProjectNo(service.getProjectNoByCodeSyncNo(codeSyncNo));  // 프로젝트 번호 조회
            history.setUserId(userId);
            history.setAction(5);

            // 생성된 파일 히스토리 저장
            service.insertRenameFileHistory(history);

            return new ResponseEntity<>("File creation history recorded successfully!", HttpStatus.OK);
        } catch (NumberFormatException e) {
            log.error("Invalid codeSyncNo format", e);
            return new ResponseEntity<>("Invalid codeSyncNo format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error inserting file creation history", e);
            return new ResponseEntity<>("Failed to record file creation history", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/getProject")
    public ResponseEntity<Object> getProject(@RequestBody Map<String, Object> request) {
        try {
            // Request에서 codeSyncNo를 String으로 받고 Integer로 변환
            String codeSyncNoStr = (String) request.get("codeSyncNo");
            int codeSyncNo = Integer.parseInt(codeSyncNoStr);

            // 서비스 호출로 프로젝트 정보 가져오기
            ProjectVO result = service.getProject(codeSyncNo);

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
    @GetMapping("/checkFolderExistence")
    public ResponseEntity<Integer> checkFolderExistence(@RequestParam Long codeSyncNo) {
        // DB에서 폴더 개수를 확인
        int count = service.checkFolderExistence(codeSyncNo);
        
        // 결과를 그대로 반환
        return ResponseEntity.ok(count);
    }
}
