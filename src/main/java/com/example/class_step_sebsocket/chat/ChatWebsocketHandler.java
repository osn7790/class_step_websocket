package com.example.class_step_sebsocket.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatWebsocketHandler implements WebSocketHandler {

    private final ChatService chatService;
    private final ConcurrentHashMap<String, WebSocketSession> sessionHashMap
            = new ConcurrentHashMap<>();

    // 맨 처음 연결을 열었을 때 콜백 됨.
    // WebSocketSession - 개별 클라이언트와 Websocket 연결을 나타내는 객체
    // - 각 클라이언트 마다 고유한 세션 ID를 가지고 있따.
    // - 메시지 전송, 연결 상태 확인, 속성 저장 등의 기능 제공해 준다.
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 어떤일을 해야 할까?
        String sessionId = session.getId();
        sessionHashMap.put(sessionId, session);
        log.info("WebSocket 연결 성공 : {}", sessionId);

        session.sendMessage(new TextMessage("서버에 연결이 되었습니다"));
    }

    // 메세지를 받았을 때 호출 됨
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = message.getPayload().toString();
        log.info("메시지 수신 {}", payload);

        // 고민 해야될 부분이 생김 ... 클라이언트가 어떤식으로 메시지를 보는 걸까?
        // 메시지 형식에 대한 파싱 - 방입장, 채팅, 귓속말, 시스템 메세지 등...
        // 메세지 프로토콜을 정의한다.
        // 클라이언트에서 CHAT: 이라고 던질 예정

        // CHAT : 안녕 반가워~
        // RoomId: 1
        if (payload.startsWith("CHAT:")) {
            String chatMessage = payload.substring(5); // CHAT: 안녕반가워~, CHAT: <-- 제거
            chatService.save(chatMessage);
            // 브로드 캐스트 처리 ..
        } else {
            // 알 수 없는 메세지 프로토콜
            session.sendMessage(new TextMessage("ERROR : 알 수 없는 메시지 형식입니다"));
        }


    }

    // 전송 에러 발생 시 호출
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket 전송 에러 : {}, {}", session.getId(), exception);
        sessionHashMap.remove(session.getId());

    }

    // Websocket 연결이 닫힐 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessionHashMap.remove(session.getId());
        log.info("Websocket 연결 종료 : {}", session.getId());
    }

    /**
     * 부분 메세지 지원 여부
     * 큰 메세지를 조각으로 나누어 전송하는 방식
     * - 채팅 메시지는 보통 짧은 텍스트만 처리하기 때문에 false로 반환
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     *
     * 연결된 모든 클라이언트에게 메세지를 보내 주는 기능
     */
    private void broadcastMessage(String message) {
        sessionHashMap.entrySet().removeIf(entry -> {
            try {
                // entry.getKey() <-- session id
                // entry.getValue() <-- WebSocketSession
                entry.getValue().sendMessage(new TextMessage(message));
                return  false; // 전송 성공시 fasle 반환 세션 유지
            } catch (Exception e) {
                log.warn("메세지 전송 실패 : {} (연결 끊김)", entry.getKey());
                return true; // 전송 실패시 true 반환 --> 세션 제거
            }
        } );
    }
}
