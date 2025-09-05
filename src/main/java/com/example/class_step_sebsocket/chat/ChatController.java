package com.example.class_step_sebsocket.chat;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RequiredArgsConstructor
@Controller // 뷰 반환
public class ChatController {

    private  final ChatService chatService;

    // 메세지 작성 폼 페이지
    @GetMapping("/save-form")
    public String saveForm() {
        return "save-form";
    }

    // 채팅 목록 페이지
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("models", chatService.findAll());
        return "index";
    }

    //채팅 메세지 저장 요청
    @PostMapping("/chat")
    public String save(String msg) {
        chatService.save(msg);
        // POST 맵핑에서 (웹 에서 주의할 점)
        return "save-form";
        // PRG 패턴
        // return "redirect:/";
    }

    /**
     *
     * @MessageMapping - 내부 동작 원리 (AOP)
     * /app/chat --> xxxxx --> 브로드 캐스트 처리
     * 순수 웹소켓 구현시 만들었던 웹 소켓 핸들러를 대체 한다
     * MessageMapping -> handleMessage() 로직을 대체 함 if --> CHAT:
     *
     * @SendTO("/topic/message") -- 기존에 로직인 broadcastMessage() 를 대체
     */

    @MessageMapping("/chat")
    @SendTo("/topic/message")
    public String sendMessage(String message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("---> STOMP 메세지 수신 : {}", message);

        try {
            if (message == null || message.trim().isEmpty()) {
                log.warn("빈 메세지 수신, 브로드 캐스트 하지 않음");
                return null;
            }

            Chat savedChat = chatService.save(message.trim());

            return savedChat.getMsg();
        } catch (Exception e) {
            log.error("메세지 저장 실패 : ", e);
            return "ERROR: 메시지 저장에 실패했습니다 ";
        }



    }


}
