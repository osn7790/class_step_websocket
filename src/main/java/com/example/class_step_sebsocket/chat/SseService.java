package com.example.class_step_sebsocket.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
@Service
public class SseService {

    // SSE 연결을 저장하는 Map 자료구조
    private final ConcurrentHashMap<String, SseEmitter> emitterHashMap = new ConcurrentHashMap<>();

    // 타임아웃 설정
    private static final long TIMEOUT = 1000 * 60 * 5;

    // 새로운 SSE 연결 생성 기능
    public SseEmitter createConnection(String clientId) {
        // 1. 객체 생성
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        // 2. 연결 정보를 가진 객체를 자료구조 map 에 저장
        emitterHashMap.put(clientId, emitter);
        log.info("새로운 연결요청 - SSE 객체 생성");

        // 3. 연결 완료/타임아웃/에러 시 콜백 등록
        emitter.onCompletion(() -> {
            log.info("연결 요청 완료");
        });

        // 타임 아웃시 클라이언트 정보 제거
        emitter.onTimeout(() -> {
            emitterHashMap.remove(clientId);
        });

        emitter.onError((e) -> {
            emitterHashMap.remove(clientId);
            log.error("SSE 연결 에러");
        });

        // 초기 메시지 전송 (연결 성공 알림)
        try {
            emitter.send(SseEmitter.event().name("connect").data("연결성공"));
        } catch (IOException e) {
            log.error("초기 메시지 전송 실패 : {}", clientId);
        }
        return emitter;

    }

    // 모든 연결된 클라이언트에게 메세지 보내기 (브로드캐스트)
    public void broadcastMessage(String message) {
        emitterHashMap.forEach((clientId, sseEmitter) -> {

            try {
                sseEmitter.send(SseEmitter.event().name("newMessage").data(message));

            } catch (IOException e) {
                log.warn("메세지 전송 실패");
                emitterHashMap.remove(clientId);
            }
        });
    }


}
