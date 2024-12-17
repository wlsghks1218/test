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
            	 // 현재 사용자가 잠근 파일 확인 및 해제
            	FileVO previouslyLockedFile = service.getLockedFileByUser(lockedBy);

            	if (previouslyLockedFile != null) {
            	    // 이전에 잠금된 파일이 있는 경우
            	    System.out.println(previouslyLockedFile.getFileNo());
            	    System.out.println(previouslyLockedFile);
            	    service.unlockFile(previouslyLockedFile);
            	    log.info("Previous lock released for file: " + previouslyLockedFile.getFileName());
            	    broadcastLockStatus(previouslyLockedFile, String.valueOf(codeSyncNo));
            	} else {
            	    // 이전에 잠금된 파일이 없는 경우
            	    log.info("No file was previously locked by user: " + lockedBy);
            	}

            
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
                log.info("File unlocked: " + file.getFileName());
                responseMessage = createResponse("success", "File unlocked successfully: " + file.getFileName(), file);
                broadcastLockStatus(file, String.valueOf(codeSyncNo));
            } else {
                log.warn("Invalid code received: " + file.getCode());
                responseMessage = createResponse("error", "Invalid operation code: " + file.getCode(), file);
            }

            sendMessage(session, responseMessage);
        } catch (Exception e) {
            log.error("Message handling error", e);
            sendMessage(session, createResponse("error", "Internal server error", null));
        }
    }

    private void broadcastLockStatus(FileVO file, String codeSyncNo) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "update");
        response.put("message", "Lock status updated");
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
