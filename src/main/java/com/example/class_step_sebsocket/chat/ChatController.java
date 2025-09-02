package com.example.class_step_sebsocket.chat;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

}
