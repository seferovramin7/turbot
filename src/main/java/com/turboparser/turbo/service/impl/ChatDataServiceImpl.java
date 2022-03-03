package com.turboparser.turbo.service.impl;

import com.turboparser.turbo.constant.ChatStage;
import com.turboparser.turbo.dto.telegram.update.ChatDTO;
import com.turboparser.turbo.dto.telegram.update.MessageDTO;
import com.turboparser.turbo.dto.telegram.update.TelegramUpdateDTO;
import com.turboparser.turbo.dto.telegram.update.UserDTO;
import com.turboparser.turbo.entity.Chat;
import com.turboparser.turbo.entity.Message;
import com.turboparser.turbo.entity.User;
import com.turboparser.turbo.repository.ChatRepository;
import com.turboparser.turbo.repository.MessageRepository;
import com.turboparser.turbo.repository.UserRepository;
import com.turboparser.turbo.service.ChatDataService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ChatDataServiceImpl implements ChatDataService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatDataServiceImpl(MessageRepository messageRepository,
                               ChatRepository chatRepository,
                               UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveTelegramMessage(TelegramUpdateDTO telegramUpdateDTO) {
        Chat chat = saveChat(telegramUpdateDTO.getMessageDTO().getChat());
        User user = saveUser(telegramUpdateDTO.getMessageDTO().getFrom());
        Message message = saveMessage(telegramUpdateDTO.getMessageDTO(), chat, user);
    }

    @Override
    public Chat saveChat(ChatDTO chatDTO) {
        Long chatId = chatDTO.getId();
        Chat chat = chatRepository.getChatByChatId(chatId);
        if (chat == null) {
            chat = new Chat();
            chat.setChatId(chatDTO.getId());
            chat.setFirstName(chatDTO.getFirstName());
            chat.setLastName(chatDTO.getLastName());
            chat.setTitle(chatDTO.getTitle());
            chat.setType(chatDTO.getType());
            chat.setUsername(chatDTO.getUsername());
            chat.setChatStage(ChatStage.START);
            chat.setBio(chatDTO.getBio());
            chat.setDescription(chatDTO.getDescription());
            chat = chatRepository.save(chat);
        }
        return chat;
    }

    @Override
    public Chat updateChat(Chat chat) {
        return chatRepository.save(chat);
    }

    @Override
    public User saveUser(UserDTO userDTO) {
        Long userId = userDTO.getId();
        User user = userRepository.getUserById(userId);
        if (user == null) {
            user = new User();
            user.setId(userDTO.getId());
            user.setIsBot(userDTO.getIsBot());
            user.setUsername(userDTO.getUsername());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setLanguageCode(userDTO.getLanguageCode());
            user = userRepository.save(user);
        }
        return user;
    }

    @Override
    public Message saveMessage(MessageDTO messageDTO, Chat chat, User user) {
        Message message = new Message();
        message.setMessageId(messageDTO.getMessageId());
        message.setDate(new Date(messageDTO.getDate()));
        message.setText(messageDTO.getText());
        message.setChat(chat);
        message.setUser(user);
        message = messageRepository.save(message);
        return message;
    }

    @Override
    public Chat getChatByChatId(Long chatId) {
        return chatRepository.getChatByChatId(chatId);
    }
}
