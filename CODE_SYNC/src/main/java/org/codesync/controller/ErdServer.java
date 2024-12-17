package org.codesync.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.codesync.domain.ChatContentVO;
import org.codesync.domain.TableVO;
import org.codesync.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.log4j.Log4j;

@Log4j
@ServerEndpoint("/chatserver.do")
public class ErdServer {

    // erdNo에 따른 세션 목록을 관리하는 맵
    private static Map<String, List<Session>> erdNoSessionMap = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Ping 메시지 전송 스케줄링

    @Autowired
    private ChatService service;

    private Gson gson = new GsonBuilder().setDateFormat("yyyy. MM. dd HH:mm:ss").create();

    @OnOpen
    public void handleOpen(Session session) {
        if (service == null) {
            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            service = context.getBean(ChatService.class);
        }

        // 클라이언트에서 전달된 erdNo 값을 통해 세션을 등록
        String erdNo = session.getRequestParameterMap().get("erdNo").get(0);

        // erdNo에 해당하는 세션 리스트가 없으면 새로 추가
        erdNoSessionMap.putIfAbsent(erdNo, new ArrayList<>());
        erdNoSessionMap.get(erdNo).add(session);

        log.info("Session connected: " + session.getId() + " for erdNo: " + erdNo);
        checkSessionList(erdNo);
        
        // 일정 시간 간격으로 Ping 메시지를 전송하는 작업을 스케줄링
        scheduler.scheduleAtFixedRate(() -> sendPing(session), 0, 30, TimeUnit.SECONDS);
    }
    
    private void sendPing(Session session) {
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendPing(null); // Ping 메시지 전송
                log.info("Ping message sent to session: " + session.getId());
            } catch (IOException e) {
                log.error("Error sending Ping message", e);
            }
        }
    }

    @OnMessage
    public void handleMessage(String msg, Session session) {
        try {
            ChatContentVO message = gson.fromJson(msg, ChatContentVO.class);
            String erdNo = message.getErdNo(); 
            TableVO tvo = gson.fromJson(msg, TableVO.class);

            // 1. DB에 실시간 메세지 등록
            if (message.getCode().equals("1")) {
                log.info("Message received for erdNo " + erdNo + ": " + message);
                service.insertChatContent(message); // DB에 메시지 저장
            }

            // erdNo에 해당하는 모든 세션에 메시지를 전송
            List<Session> sessions = erdNoSessionMap.get(erdNo);
            if (sessions != null) {
                for (Session s : sessions) {
                    if (s.isOpen()) {
                        s.getBasicRemote().sendText(msg);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Message handling error", e);
        }
    }

    @OnClose
    public void handleClose(Session session) {
        // erdNo에 해당하는 세션 목록에서 해당 세션을 제거
        for (String erdNo : erdNoSessionMap.keySet()) {
            erdNoSessionMap.get(erdNo).remove(session);
        }
        log.info("Session closed: " + session.getId());
    }

    private void checkSessionList(String erdNo) {
        System.out.println();
        System.out.println("[Session List for erdNo: " + erdNo + "]");
        List<Session> sessions = erdNoSessionMap.get(erdNo);
        if (sessions != null) {
            for (Session session : sessions) {
                System.out.println(session.getId());
            }
        }
        System.out.println();
    }
}