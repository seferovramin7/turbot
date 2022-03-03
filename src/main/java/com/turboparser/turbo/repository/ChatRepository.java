package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Chat getChatByChatId(Long chatId);

}
