package com.turboparser.turbo.service;

import com.turboparser.turbo.dto.telegram.send.SendMessageResponseDTO;
import com.turboparser.turbo.dto.telegram.send.text.SendMessageDTO;
import com.turboparser.turbo.dto.telegram.update.TelegramUpdateDTO;

import java.io.IOException;
import java.text.ParseException;

public interface TelegramMessagingService {

    TelegramUpdateDTO getUpdates();

    SendMessageResponseDTO sendMessage(SendMessageDTO sendMessageDTO);

    SendMessageResponseDTO reply(TelegramUpdateDTO telegramUpdateDTO) throws IOException, ParseException;
}
