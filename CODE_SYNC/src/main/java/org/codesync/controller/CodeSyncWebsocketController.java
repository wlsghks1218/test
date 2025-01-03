package org.codesync.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.codesync.domain.FileVO;
import org.codesync.service.CodeSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.log4j.Log4j;

@CrossOrigin(origins = "*")
@Log4j
@Controller
@ServerEndpoint("/codeSync.do")
public class CodeSyncWebsocketController {

    private static Map<String, List<Session>> SessionMap = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    private CodeSyncService service;

    private Gson gson = new GsonBuilder().setDateFormat("yyyy. MM. dd HH:mm:ss").create();

    @OnOpen
    public void handleOpen(Session session) {
        if (service == null) {
            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            service = context.getBean(CodeSyncService.class);
        }

        String codeSyncNo = session.getRequestParameterMap().get("codeSyncNo").get(0);
        SessionMap.putIfAbsent(codeSyncNo, new ArrayList<>());
        SessionMap.get(codeSyncNo).add(session);

        log.info("Session connected: " + session.getId() + " for codeSyncNo: " + codeSyncNo);
        checkSessionList(codeSyncNo);

        scheduler.scheduleAtFixedRate(() -> sendPing(session), 0, 30, TimeUnit.SECONDS);
    }

    private void sendPing(Session session) {
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendPing(null);
                log.info("Ping message sent to session: " + session.getId());
            } catch (IOException e) {
                log.error("Error sending Ping message", e);
            }
        }
    }

    @OnMessage
    public void handleMessage(String msg, Session session) {
        try {
            FileVO file = gson.fromJson(msg, FileVO.class);
            int codeSyncNo = file.getCodeSyncNo();
            log.info("Message received for codeSyncNo " + codeSyncNo + ": " + file);
            int lockedBy = file.getLockedBy();

            String responseMessage;

            if ("3".equals(file.getCode())) { // Lock file
              
                // 사용자가 이전에 잠근 파일 확인 및 해제
                FileVO previouslyLockedFile = service.getLockedFileByUser(lockedBy);
                if (previouslyLockedFile != null) {
                    service.unlockFile(previouslyLockedFile);
                    log.info("Previous lock released for file: " + previouslyLockedFile.getFileName());
                    broadcastLockStatus(previouslyLockedFile, String.valueOf(codeSyncNo));
                }

                // 현재 파일 잠금 처리
                boolean isLocked = service.checkFileLock(file);
                if (!isLocked) {
                    service.lockFile(file);
                    log.info("File locked: " + file.getFileName());
                    responseMessage = createResponse("success", "File locked successfully: " + file.getFileName(), file);
                    broadcastLockStatus(file, String.valueOf(codeSyncNo));
                } else {
                    log.info("File already locked: " + file.getFileName());
                    responseMessage = createResponse("error", "File is already locked: " + file.getFileName(), file);
                }
            } else if ("4".equals(file.getCode())) { // Unlock file
                service.unlockFile(file);
                responseMessage = createResponse("success", "File unlocked successfully: " + file.getFileName(), file);
                broadcastLockStatus(file, String.valueOf(codeSyncNo));
            } else if ("6".equals(file.getCode())) {  // 폴더 트리 전체 삭제를 위한 메서드
                // 1. codeSyncNo로 해당하는 폴더들의 folderNo를 가져오기
                List<Integer> folderNos = service.getFolderNosByCodeSyncNo(codeSyncNo);
                if (folderNos.isEmpty()) {
                    responseMessage = createResponse("error", "No folders found for codeSyncNo: " + codeSyncNo, file);
                    sendMessage(session, responseMessage);
                    return;
                }

                // 2. 각 folderNo로 해당하는 파일들을 가져오기
                List<Integer> fileNosToDelete = new ArrayList<>();
                for (Integer folderNo : folderNos) {
                    List<Integer> fileNos = service.getFileNosByFolderNo(folderNo);
                    fileNosToDelete.addAll(fileNos);
                }

                // 3. 폴더와 파일들 삭제 처리
                boolean deleteSuccess = service.deleteFoldersAndFiles(folderNos, fileNosToDelete);
                if (deleteSuccess) {
                    responseMessage = createResponse("success", "Folders and files deleted successfully", file);
                } else {
                    responseMessage = createResponse("error", "Error occurred while deleting folders and files", file);
                }

                sendMessage(session, responseMessage);
                broadcastLockStatus(file, String.valueOf(codeSyncNo));
            }
            else if ("7".equals(file.getCode())) { 
            	 if (file.getFileName() == null) { 
            	        String newName = file.getNewName();
            	        String folderName = file.getFolderName();
            	        
            	        service.changeFolderName(newName,folderName,codeSyncNo);
            	        
            	    } else if (file.getFolderName() == null) {
            	       String newName = file.getNewName();
            	       String fileName = file.getFileName();
            	       int folderNo = file.getFolderNo();
            	       
            	       service.changeFileName(newName,fileName,folderNo);
            	    }
            	 responseMessage = createResponse("success", "reNamed Success", file);
            	 sendMessage(session, responseMessage);
                 broadcastLockStatus(file, String.valueOf(codeSyncNo));
            }else if ("8".equals(file.getCode())) { // Unlock file
            
                int folderNo = file.getFolderNo();
                service.deleteFolder(folderNo);
           	 responseMessage = createResponse("success", "folder Delete success", file);
        	 sendMessage(session, responseMessage);
             broadcastLockStatus(file, String.valueOf(codeSyncNo));
            } else if ("9".equals(file.getCode())) { // Unlock file
           
               service.createFile(file);
            	 responseMessage = createResponse("success", "file create success", file);
            	 sendMessage(session, responseMessage);
                 broadcastLockStatus(file, String.valueOf(codeSyncNo));
            }else if ("10".equals(file.getCode())) { // Unlock file
                
                service.createFolder(file);
             	 responseMessage = createResponse("success", "folder create success", file);
             	 sendMessage(session, responseMessage);
                  broadcastLockStatus(file, String.valueOf(codeSyncNo));
             }else if ("11".equals(file.getCode())) { // Unlock file
              
            	 if (file.getType().equals("folder")) {
            	    service.pastefolder(file);
				}else if (file.getType().equals("file")) {
					service.pasteFile(file);
				}      
            	 responseMessage = createResponse("success", "folder create success", file);
              	 sendMessage(session, responseMessage);
                   broadcastLockStatus(file, String.valueOf(codeSyncNo));
              } else if ("12".equals(file.getCode())) { // Unlock file
                  service.deleteFile(file);
                  responseMessage = createResponse("success", "File delete successfully: " + file.getFileName(), file);
                  broadcastLockStatus(file, String.valueOf(codeSyncNo));
              } else {
                log.warn("Invalid code received: " + file.getCode());
                responseMessage = createResponse("error", "Invalid operation code: " + file.getCode(), file);
                sendMessage(session, responseMessage);
            }

        } catch (Exception e) {
            log.error("Message handling error", e);
            sendMessage(session, createResponse("error", "Internal server error", null));
        }
    }

    private void broadcastLockStatus(FileVO file, String codeSyncNo) {
        Map<String, Object> response = new HashMap<>();
     

        // 기존 처리 로직 유지: file.getCode()에 따라 메시지 설정
        if ("3".equals(file.getCode())) { // 파일 잠금
            response.put("message", "File locked");
        } else if ("4".equals(file.getCode())) { // 파일 잠금 해제
            response.put("message", "File unlocked");
        } else if ("5".equals(file.getCode())) { // 잠금 상태 확인
            // 잠금 상태에 따라 메시지 커스터마이징
            int lockStatus = file.getIsLockedByUser(); // file.getIsLockedByUser는 int로 변경됨
            if (lockStatus == 1) {
                response.put("message", "File is not locked.");
            } else if (lockStatus == 2) {
                response.put("message", "File is locked by the user.");
            } else if (lockStatus == 3) {
                response.put("message", "File is locked by another user.");
             
            }
        }else if ("6".equals(file.getCode())) { // 파일 잠금 해제
            response.put("message", "File delete");
            response.put("status", "delete_complete");
            
        }else if ("7".equals(file.getCode())) { // 파일 잠금 해제
            response.put("message", "name changed");
            response.put("status", "change_complete");
            
        }else if ("8".equals(file.getCode())) { // 파일 잠금 해제
            response.put("message", "folder Delete");
            response.put("status", "Delete_complete");
            
        }else if ("9".equals(file.getCode())) { // 파일 잠금 해제
            response.put("message", "file Create");
            response.put("status", "Create_complete");
            
        }else if ("10".equals(file.getCode())) { // 파일 잠금 해제
            response.put("message", "folder Create");
            response.put("status", "Create_complete");
            
        }else if ("11".equals(file.getCode())) { // 파일 잠금 해제
        	if (file.getType().equals("folder")) {
        		response.put("message", "folder Paste");
        		response.put("status", "Paste_complete");
				
			}else {
				response.put("message", "file Paste");
        		response.put("status", "Paste_complete");
			}
            
        }else if ("12".equals(file.getCode())) { // 파일 잠금 해제
            response.put("message", "file Delete");
            response.put("status", "Delete_complete");
            
        }

        response.put("file", file);

        String message = gson.toJson(response);

        List<Session> sessions = SessionMap.get(codeSyncNo);
        if (sessions != null) {
            for (Session s : sessions) {
                sendMessage(s, message);
            }
        }
    }
  


    private String createResponse(String status, String message, FileVO file) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        if (file != null) {
            response.put("file", file);
        }
        return gson.toJson(response);
    }

    private void sendMessage(Session session, String message) {
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("Failed to send message to session: " + session.getId(), e);
            }
        }
    }

    @OnClose
    public void handleClose(Session session) {
        for (String codeSyncNo : SessionMap.keySet()) {
            SessionMap.get(codeSyncNo).remove(session);
            if (SessionMap.get(codeSyncNo).isEmpty()) {
                SessionMap.remove(codeSyncNo);
            }
        }
        log.info("Session closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("WebSocket Error: " + throwable.getMessage(), throwable);
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException e) {
            log.error("Error closing session", e);
        }
    }

    private void checkSessionList(String codeSyncNo) {
        log.info("[Session List for codeSyncNo: " + codeSyncNo + "]");
        List<Session> sessions = SessionMap.get(codeSyncNo);
        if (sessions != null) {
            for (Session session : sessions) {
                log.info("Session ID: " + session.getId());
            }
        }
    }
}
