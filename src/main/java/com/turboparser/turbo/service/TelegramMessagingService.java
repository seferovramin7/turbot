package com.turboparser.turbo.service;

import com.turboparser.turbo.dto.telegram.send.SendMessageResponseDTO;
import com.turboparser.turbo.dto.telegram.send.photo.SendPhotoDTO;
import com.turboparser.turbo.dto.telegram.send.text.SendMessageDTO;
import com.turboparser.turbo.dto.telegram.update.TelegramUpdateDTO;
import com.turboparser.turbo.entity.Home;

import java.util.List;

public interface TelegramMessagingService {

    TelegramUpdateDTO getUpdates();

    SendMessageResponseDTO sendMessage(SendMessageDTO sendMessageDTO);

    SendMessageResponseDTO sendPhoto(SendPhotoDTO sendPhotoDTO);

    SendMessageResponseDTO reply(TelegramUpdateDTO telegramUpdateDTO);

    void sendNewNotifications(List<Home> homeList);

}
