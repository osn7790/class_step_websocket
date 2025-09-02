package com.example.class_step_sebsocket.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRepository chatRepository;

    // 채팅 메세지 저장

    @Transactional // 쓰기 작업이므로 읽기 전용 해제 처리
    public Chat save(String msg) {
        Chat chat = Chat.builder().msg(msg).build();
        return chatRepository.save(chat);
    }

    // 채팅 메세지 리스트
    public List<Chat> findAll() {
        // 내림 차순으로 정렬하고 싶다면
        Sort desc = Sort.by(Sort.Direction.DESC, "id");
        return chatRepository.findAll();
    }


}
