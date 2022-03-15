package com.turboparser.turbo.schedule;

import com.turboparser.turbo.dto.telegram.send.text.NotificationDTO;
import com.turboparser.turbo.dto.telegram.update.TelegramUpdateDTO;
import com.turboparser.turbo.entity.SearchParameter;
import com.turboparser.turbo.repository.SearchParameterRepository;
import com.turboparser.turbo.service.RequestCreationService;
import com.turboparser.turbo.service.TelegramMessagingService;
import com.turboparser.turbo.service.impl.TelegramMessagingServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
@EnableAsync
@Slf4j
public class BotSchedule {

    private final TelegramMessagingService telegramMessagingService;

    private final SearchParameterRepository searchParameterRepository;

    private final RequestCreationService requestCreationService;
    private final TelegramMessagingServiceImpl telegramMessagingServiceImpl;


    public BotSchedule(
            TelegramMessagingService telegramMessagingService, SearchParameterRepository searchParameterRepository, RequestCreationService requestCreationService, TelegramMessagingServiceImpl telegramMessagingServiceImpl) {
        this.telegramMessagingService = telegramMessagingService;
        this.searchParameterRepository = searchParameterRepository;
        this.requestCreationService = requestCreationService;
        this.telegramMessagingServiceImpl = telegramMessagingServiceImpl;
    }

    @Scheduled(fixedRateString = "${task.update-telegram-update.rate}")
    public void getTelegramUpdates() throws IOException, ParseException {
        TelegramUpdateDTO telegramUpdateDTO = telegramMessagingService.getUpdates();
        if (telegramUpdateDTO != null) {
            log.info(telegramUpdateDTO.toString());
            telegramMessagingService.reply(telegramUpdateDTO);
        }
    }

    @Scheduled(fixedRateString = "${task.update-cars.rate}")
    public void checkForTurboUpdates() throws IOException, ParseException {
        List<SearchParameter> archivedCars = searchParameterRepository.findAll();
        for (SearchParameter element : archivedCars) {
            try {
                List<NotificationDTO> responseList = requestCreationService.createRequest(element);
                for (NotificationDTO reponse : responseList) {
                    telegramMessagingServiceImpl.sendMessage(
                            telegramMessagingServiceImpl.getNewCarMessage(element.getChat().getChatId(), reponse.toString()));
                }
            } catch (NullPointerException e) {
                System.out.println("No any cars of this type : " + element.toString());
            }
        }
    }
}
