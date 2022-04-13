package com.turboparser.turbo.schedule;

import com.turboparser.turbo.dto.telegram.send.SendMessageResponseDTO;
import com.turboparser.turbo.dto.telegram.send.text.NotificationDTO;
import com.turboparser.turbo.dto.telegram.update.TelegramUpdateDTO;
import com.turboparser.turbo.entity.Chat;
import com.turboparser.turbo.entity.SearchParameter;
import com.turboparser.turbo.entity.SpecificVehicleSearchParameter;
import com.turboparser.turbo.repository.SearchParameterRepository;
import com.turboparser.turbo.repository.SpecificVehicleRepository;
import com.turboparser.turbo.service.ChatDataService;
import com.turboparser.turbo.service.MessageReceiverService;
import com.turboparser.turbo.service.impl.MessageReceiverServiceImpl;
import com.turboparser.turbo.service.impl.RequestCreationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final MessageReceiverService messageReceiverService;
    private final ChatDataService chatDataService;
    private final SearchParameterRepository searchParameterRepository;
    private final RequestCreationService requestCreationService;
    private final MessageReceiverServiceImpl messageReceiverServiceImpl;
    private final SpecificVehicleRepository specificVehicleRepository;
    @Value("${turbo.url}")
    private String turboLink;

    public BotSchedule(
            MessageReceiverService messageReceiverService, ChatDataService chatDataService, SearchParameterRepository searchParameterRepository, RequestCreationService requestCreationService, MessageReceiverServiceImpl messageReceiverServiceImpl, SpecificVehicleRepository specificVehicleRepository) {
        this.messageReceiverService = messageReceiverService;
        this.chatDataService = chatDataService;
        this.searchParameterRepository = searchParameterRepository;
        this.requestCreationService = requestCreationService;
        this.messageReceiverServiceImpl = messageReceiverServiceImpl;
        this.specificVehicleRepository = specificVehicleRepository;
    }

    @Scheduled(fixedRateString = "${task.update-telegram-update.rate}")
    public void getTelegramUpdates() throws IOException, ParseException {
        TelegramUpdateDTO telegramUpdateDTO = messageReceiverService.getUpdates();
        if (telegramUpdateDTO != null) {
            log.info(telegramUpdateDTO.toString());
            messageReceiverService.reply(telegramUpdateDTO);
        }
    }

    @Scheduled(fixedRateString = "${task.update-cars.rate}")
    public void checkForTurboUpdates() throws IOException, ParseException {
        List<SearchParameter> archivedCars = searchParameterRepository.findAll();
        for (SearchParameter element : archivedCars) {
            try {
                List<NotificationDTO> responseList = requestCreationService.createRequest(element);
                for (NotificationDTO response : responseList) {
                    SendMessageResponseDTO sendMessageResponseDTO = messageReceiverServiceImpl.sendMessage(messageReceiverServiceImpl
                            .getNewCarMessage(element.getChat().getChatId(), response.toString()));
                    if (sendMessageResponseDTO.getOk()) {
                        if (element.getChat().getReqLimit() > 0 || element.getChat().getReqLimit() != null) {
                            Chat chat = element.getChat();
                            chat.setReqLimit(element.getChat().getReqLimit() - 1);
                            element.setChat(chat);
                            chatDataService.updateChat(chat);
                        }
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("No any cars of this type : " + element.toString());
            }
        }
    }

    @Scheduled(fixedRateString = "99999")
    public void deleteRecords() throws IOException, ParseException {
        List<SearchParameter> all = searchParameterRepository.findAll();
        System.out.println("-------------------------------------------------------");

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getCurrency() == null) {
                System.out.println(all.get(i).getMake());
                searchParameterRepository.delete(all.get(i));
            }
            System.out.println("-------------------------------------------------------");

        }
    }

    @Scheduled(fixedRateString = "${task.update-cars.rate}")
    public void checkForSpecificUpdates() throws IOException, ParseException {
        List<SpecificVehicleSearchParameter> archivedCars = specificVehicleRepository.findAll();
        for (SpecificVehicleSearchParameter element : archivedCars) {
            try {
                SpecificVehicleSearchParameter newSpecificVehicleSearchParameter = requestCreationService.createSpecificRequest(element.getLotId().toString());
                if (newSpecificVehicleSearchParameter == null) {
                    messageReceiverServiceImpl.sendMessage(
                            messageReceiverServiceImpl.getNoAnyCarFoundMessage(element.getChat().getChatId(), element.getChat().getLanguage(), element.getGeneralInfo()));
                } else {
                    List<SpecificVehicleSearchParameter> allByLotId = specificVehicleRepository.findAllByLotId(element.getLotId());
                    SpecificVehicleSearchParameter oldSpecificVehicleSearchParameter = allByLotId.get(allByLotId.size() - 1);
                    Long chatId = oldSpecificVehicleSearchParameter.getChat().getChatId();
                    if (!oldSpecificVehicleSearchParameter.getPrice().equals(newSpecificVehicleSearchParameter.getPrice())
                            ||
                            !oldSpecificVehicleSearchParameter.getGeneralInfo().equals(newSpecificVehicleSearchParameter.getGeneralInfo())
                    ) {
                        messageReceiverServiceImpl.sendMessage(
                                messageReceiverServiceImpl.getChangedSpecificCarMessage(element.getChat().getChatId(),
                                        newSpecificVehicleSearchParameter,
                                        oldSpecificVehicleSearchParameter,
                                        element.getChat().getLanguage()));
                        messageReceiverServiceImpl.saveSpecialCarUpdateToDB(newSpecificVehicleSearchParameter, chatId);
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("No any cars of this type : " + element.toString());
            }
        }
    }
}
