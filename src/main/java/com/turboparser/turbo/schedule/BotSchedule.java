package com.turboparser.turbo.schedule;

import lombok.extern.slf4j.Slf4j;
import com.turboparser.turbo.dto.telegram.update.TelegramUpdateDTO;
import com.turboparser.turbo.service.TelegramMessagingService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
@Slf4j
public class BotSchedule {

    private final TelegramMessagingService telegramMessagingService;

    public BotSchedule(
                       TelegramMessagingService telegramMessagingService) {
        this.telegramMessagingService = telegramMessagingService;
    }

    @Scheduled(fixedRateString = "${task.update-telegram-update.rate}")
    public void getTelegramUpdates() {
        TelegramUpdateDTO telegramUpdateDTO = telegramMessagingService.getUpdates();
        if (telegramUpdateDTO != null) {
            log.info(telegramUpdateDTO.toString());
            telegramMessagingService.reply(telegramUpdateDTO);
        }
    }
}
