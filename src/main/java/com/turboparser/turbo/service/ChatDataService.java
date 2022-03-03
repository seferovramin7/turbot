package com.turboparser.turbo.service;

import com.turboparser.turbo.dto.telegram.update.ChatDTO;
import com.turboparser.turbo.dto.telegram.update.MessageDTO;
import com.turboparser.turbo.dto.telegram.update.TelegramUpdateDTO;
import com.turboparser.turbo.dto.telegram.update.UserDTO;
import com.turboparser.turbo.entity.Chat;
import com.turboparser.turbo.entity.Message;
import com.turboparser.turbo.entity.User;

public interface ChatDataService {

    void saveTelegramMessage(TelegramUpdateDTO telegramUpdateDTO);

    Chat saveChat(ChatDTO chatDTO);

    Chat updateChat(Chat chat);

    User saveUser(UserDTO userDTO);

    Message saveMessage(MessageDTO messageDTO, Chat chat, User user);

    Chat getChatByChatId(Long chatId);

}
