package com.example.class_step_sebsocket.chat;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    // 기본적인 CRUD 완성
}
