package com.turboparser.turbo.service.impl;

import com.turboparser.turbo.constant.ChatStage;
import com.turboparser.turbo.constant.Language;
import com.turboparser.turbo.dto.telegram.send.KeyboardButtonDTO;
import com.turboparser.turbo.dto.telegram.send.ReplyKeyboardMarkupDTO;
import com.turboparser.turbo.dto.telegram.send.ReplyKeyboardRemoveDTO;
import com.turboparser.turbo.dto.telegram.send.SendMessageResponseDTO;
import com.turboparser.turbo.dto.telegram.send.text.SendMessageDTO;
import com.turboparser.turbo.dto.telegram.update.TelegramResponseDTO;
import com.turboparser.turbo.dto.telegram.update.TelegramUpdateDTO;
import com.turboparser.turbo.entity.*;
import com.turboparser.turbo.repository.SearchParameterRepository;
import com.turboparser.turbo.repository.SpecificVehicleRepository;
import com.turboparser.turbo.service.*;
import com.turboparser.turbo.util.CarTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@Service
public class TelegramMessagingServiceImpl implements TelegramMessagingService {

    private final HttpRequestService httpRequestService;
    private final ChatDataService chatDataService;
    private final MessageProvider messageProvider;
    private final MakeService makeService;
    private final ModelService modelService;
    private final SearchParameterService searchParameterService;
    private final RequestCreationService requestCreationService;
    private final CarTypeMapper carTypeMapper;
    private final SearchParameterRepository searchParameterRepository;
    private final SpecificVehicleRepository specificVehicleRepository;

    @Value("${telegram.api.base-url}")
    private String telegramApiBaseUrl;
    @Value("${telegram.api.token}")
    private String botToken;
    @Value("${telegram.bot.name}")
    private String botName;
    private Long offset = null;


    public TelegramMessagingServiceImpl(HttpRequestService httpRequestService,
                                        ChatDataService chatDataService,
                                        MessageProvider messageProvider,
                                        MakeService makeService,
                                        ModelService modelService,
                                        SearchParameterService searchParameterService,
                                        RequestCreationService requestCreationService,
                                        CarTypeMapper carTypeMapper,
                                        SearchParameterRepository searchParameterRepository, SpecificVehicleRepository specificVehicleRepository) {
        this.httpRequestService = httpRequestService;
        this.chatDataService = chatDataService;
        this.messageProvider = messageProvider;
        this.makeService = makeService;
        this.modelService = modelService;
        this.searchParameterService = searchParameterService;
        this.requestCreationService = requestCreationService;
        this.carTypeMapper = carTypeMapper;
        this.searchParameterRepository = searchParameterRepository;
        this.specificVehicleRepository = specificVehicleRepository;
    }

    @Override
    public TelegramUpdateDTO getUpdates() {
        String url = telegramApiBaseUrl + "/bot" + botToken + "/getUpdates";
        if (offset != null)
            url = url + "?offset=" + offset;
        TelegramResponseDTO telegramResponseDTO = httpRequestService.sendGetRequest(url, TelegramResponseDTO.class);
        if (telegramResponseDTO.getResult().size() > 0) {
            if (telegramResponseDTO.getResult().get(0).getMessageDTO() != null) {
                TelegramUpdateDTO telegramUpdateDTO = telegramResponseDTO.getResult().get(0);
                log.info(telegramUpdateDTO.toString());
                telegramUpdateDTO.getMessageDTO().setDate(telegramUpdateDTO.getMessageDTO().getDate() * 1000);
                chatDataService.saveTelegramMessage(telegramUpdateDTO);
                offset = telegramUpdateDTO.getUpdateId() + 1;
                return telegramUpdateDTO;
            } else {
                offset = telegramResponseDTO.getResult().get(0).getUpdateId() + 1;
                return null;
            }
        } else
            return null;
    }

    @Override
    public SendMessageResponseDTO sendMessage(SendMessageDTO sendMessageDTO) {
        String url = telegramApiBaseUrl + "/bot" + botToken + "/sendMessage";
        SendMessageResponseDTO responseDTO = httpRequestService.sendPostRequest(url, sendMessageDTO, SendMessageResponseDTO.class);
        return responseDTO;
    }

    @Override
    public SendMessageResponseDTO reply(TelegramUpdateDTO telegramUpdateDTO) throws IOException, ParseException {
        // check it is private or group chat
        if (telegramUpdateDTO.getMessageDTO().getChat().getType().equals("group")) {
            String callName = "@" + botName;
            if (telegramUpdateDTO.getMessageDTO().getText().startsWith(callName)) {
                telegramUpdateDTO.getMessageDTO().setText(telegramUpdateDTO.getMessageDTO().getText().substring(callName.length()).trim());
            } else if (telegramUpdateDTO.getMessageDTO().getReplyToMessage() != null) {
                if (!telegramUpdateDTO.getMessageDTO().getReplyToMessage().getFrom().getUsername().equals(botName))
                    return null;
            } else
                return null;
        }

        Long chatId = telegramUpdateDTO.getMessageDTO().getChat().getId();
        String text = telegramUpdateDTO.getMessageDTO().getText().trim();
        Long messageId = telegramUpdateDTO.getMessageDTO().getMessageId();
        Chat chat = chatDataService.getChatByChatId(chatId);


        if (text.equals("/language")) {
            sendMessage(getLanguageChoiceMessage(chatId));
            chat.setChatStage(ChatStage.LANGUAGE);
            chat = chatDataService.updateChat(chat);
        } else if (chat.getChatStage() == ChatStage.LANGUAGE) {
            if (text.equals("azərbaycanca") || text.equals("english") || text.equals("русский")) {
                switch (text) {
                    case "azərbaycanca":
                        chat.setLanguage(Language.az);
                        chat = chatDataService.updateChat(chat);
                        break;
                    case "русский":
                        chat.setLanguage(Language.ru);
                        chat = chatDataService.updateChat(chat);
                        break;
                    case "english":
                        chat.setLanguage(Language.en);
                        chat = chatDataService.updateChat(chat);
                        break;
                }
                return sendMessage(getLanguageMessage(chatId, chat.getLanguage()));
            }
        }

        if (text.equals("/all")) {
            searchParameterService.deleteAllByModel(null);
            List<SearchParameter> allByChatId = searchParameterRepository.getAllByChat_ChatId(chatId);
            for (SearchParameter element : allByChatId) {
                if (element.getModel() != null) {
                    String allResult =
                            element.getMake() + " " + element.getModel() + "\n"
                                    + "Min : " + element.getMinPrice() + " AZN" + "\n"
                                    + "Max : " + element.getMaxPrice() + " AZN" + "\n"
                                    + "From : " + element.getMinYear() + "\n"
                                    + "To : " + element.getMaxYear();
                    sendMessage(getAllSearchMessage(chatId, allResult));
                } else {
                    searchParameterRepository.deleteById(element.getId());
                }
            }
            return null;
        }

        if (text.equals("/new")) {
            chat.setChatStage(ChatStage.START);
            chat = chatDataService.updateChat(chat);
        }


        if (text.equals("/newspecific")) {
            chat.setChatStage(ChatStage.SPECIFIC);
            chat = chatDataService.updateChat(chat);
            sendMessage(getSpecificInfoMessage(chatId, chat.getLanguage()));
        } else if (chat.getChatStage() == ChatStage.SPECIFIC) {
            SpecificVehicleSearchParameter newSpecificVehicleSearchParameter = requestCreationService.createSpecificRequest(text);
            if (newSpecificVehicleSearchParameter != null) {
                if (specificVehicleRepository.findByLotId(Long.parseLong(text.split("autos/")[1].split("-")[0])) == null) {
                    newSpecificVehicleSearchParameter.setChat(chat);
                    specificVehicleRepository.save(newSpecificVehicleSearchParameter);
                    sendMessage(getSpecificAddMessage(chatId, chat.getLanguage(), newSpecificVehicleSearchParameter));
                } else {
                    if (newSpecificVehicleSearchParameter.getClass() == specificVehicleRepository.findByLotId(newSpecificVehicleSearchParameter.getLotId()).getClass()) {
                        sendMessage(getAlreadyExistisMessage(chatId, chat.getLanguage()));
                        return null;
                    }
                }
            } else {
                sendMessage(notFoundAnySpecialCar(chatId, chat.getLanguage()));
            }
            chat.setChatStage(ChatStage.NONE);
            chat = chatDataService.updateChat(chat);
        }

        if (text.equals("/deletespecific")) {
            chat.setChatStage(ChatStage.SPECIFIC_DELETE);
            chatDataService.updateChat(chat);
            SendMessageResponseDTO sendMessageResponseDTO = sendMessage(getSpecificDeleteMessage(chatId, chat.getLanguage()));
            return sendMessageResponseDTO;
        } else if (chat.getChatStage() == (ChatStage.SPECIFIC_DELETE)) {
            SpecificVehicleSearchParameter byChat_chatIdAndGeneralInfo = specificVehicleRepository.findByChat_ChatIdAndGeneralInfo(chatId, text);
            specificVehicleRepository.delete(byChat_chatIdAndGeneralInfo);
            chat.setChatStage(ChatStage.NONE);
            chat = chatDataService.updateChat(chat);
        }
        if (text.equals("/delete")) {
            searchParameterService.deleteAllByModel(null);
            chat.setChatStage(ChatStage.DELETE);
            chatDataService.updateChat(chat);
            return sendMessage(getDeleteMessage(chatId, chat.getLanguage()));
        } else if (chat.getChatStage() == (ChatStage.DELETE)) {
            String[] parts = text.split("\\s", 2);
            String make = parts[0];
            String model = parts[1];
            searchParameterService.deleteSearchParameterByMakeAndModel(chatId, make, model);
            chat.setChatStage(ChatStage.NONE);
            chat = chatDataService.updateChat(chat);
        }

        if (chat.getChatStage() == ChatStage.START) {
            chat.setChatStage(ChatStage.CAR_MAKE);
            chat = chatDataService.updateChat(chat);
            return sendMessage(getMakeChoiceMessage(chatId, chat.getLanguage()));
        }
        // Make select
        else if (chat.getChatStage() == ChatStage.CAR_MAKE) {
            MakeEntity make = makeService.getMakeByMakeName(text);
            int makeId = make.getMakeId();
            if (make != null) {
                SearchParameter searchParameter = new SearchParameter();
                searchParameter.setChat(chat);
                searchParameter.setMessageId(messageId);
                searchParameter.setMake(make.getMake());
                searchParameterService.saveSearchParameter(searchParameter);
                chat.setChatStage(ChatStage.CAR_MODEL);
                chatDataService.updateChat(chat);
                return sendMessage(getModelChoiceMessage(chatId, chat.getLanguage(), makeId));
            } else {
                return sendMessage(getMakeChoiceMessage(chatId, chat.getLanguage()));
            }
        }
        // Car Model
        else if (chat.getChatStage() == ChatStage.CAR_MODEL) {
            String model = "";
            model = text;
            SearchParameter searchParameter = searchParameterService.getSearchParameterByMaxMessageId(chatId);
            searchParameter.setModel(model);
            searchParameterService.updateSearchParameter(searchParameter);
            chat.setChatStage(ChatStage.PRICE_MIN);
            chatDataService.updateChat(chat);
            return sendMessage(getPriceQuestionMessage(chatId, chat.getLanguage(), chat.getChatStage() == ChatStage.PRICE_MIN));
        } else if (chat.getChatStage() == ChatStage.PRICE_MIN || chat.getChatStage() == ChatStage.PRICE_MAX) {
            // check if this parameter was skipped
            if (text.equals(messageProvider.getMessage("skip_button", chat.getLanguage()))) {
            } else {
                Long enteredPrice = null;
                try {
                    enteredPrice = Long.parseLong(text);
                } catch (NumberFormatException ex) {
                    log.error("Incorrect price. Entered value: " + enteredPrice);
                    sendMessage(getInvalidNumberErrorMessage(chatId, chat.getLanguage()));
                    return sendMessage(getPriceQuestionMessage(chatId, chat.getLanguage(), chat.getChatStage() == ChatStage.PRICE_MIN));
                }
                SearchParameter searchParameter = searchParameterService.getSearchParameterByMaxMessageId(chatId);
                if (chat.getChatStage() == ChatStage.PRICE_MIN) {
                    searchParameter.setMinPrice(enteredPrice);
                } else {
                    searchParameter.setMaxPrice(enteredPrice);
                }
                searchParameterService.updateSearchParameter(searchParameter);
            }
            if (chat.getChatStage() == ChatStage.PRICE_MIN) {
                chat.setChatStage(ChatStage.PRICE_MAX);
                chatDataService.updateChat(chat);
                return sendMessage(getPriceQuestionMessage(chatId, chat.getLanguage(), false));
            } else {
                chat.setChatStage(ChatStage.YEAR_MIN);
                chatDataService.updateChat(chat);
                return sendMessage(getYearQuestionMessage(chatId, chat.getLanguage(), true));
            }
        } else if (chat.getChatStage() == ChatStage.YEAR_MIN || chat.getChatStage() == ChatStage.YEAR_MAX) {
            if (text.equals(messageProvider.getMessage("skip_button", chat.getLanguage()))) {
            } else {
                Long enteredNumber = null;
                try {
                    SearchParameter searchParameter = searchParameterService.getSearchParameterByMaxMessageId(chatId);
                    if (chat.getChatStage() == ChatStage.YEAR_MIN) {
                        enteredNumber = Long.parseLong(text);
                        searchParameter.setMinYear(enteredNumber);
                        searchParameterService.updateSearchParameter(searchParameter);
                        chat.setChatStage(ChatStage.YEAR_MAX);
                        chatDataService.updateChat(chat);
                        return sendMessage(getYearQuestionMessage(chatId, chat.getLanguage(), false));
                    } else {
                        chat.setChatStage(ChatStage.YEAR_MAX);
                        enteredNumber = Long.parseLong(text);
                        searchParameter.setMaxYear(enteredNumber);
                        chatDataService.updateChat(chat);
                        searchParameterService.updateSearchParameter(searchParameter);
                    }
                    searchParameter = searchParameterService.getSearchParameterByMaxMessageId(chatId);
                    searchParameter = searchParameterService.updateSearchParameter(searchParameter);
                    chat.setChatStage(ChatStage.READY_RECEIVED);
                    chat = chatDataService.updateChat(chat);
                    if (searchParameter == null)
                        searchParameter = searchParameterService.getSearchParameterByMaxMessageId(chatId);
                    sendMessage(getSearchParametersFinishMessage(chatId, chat.getLanguage(), searchParameter));
                } catch (NumberFormatException ex) {
                    log.error("Incorrect price. Entered value: " + text);
                    sendMessage(getInvalidNumberErrorMessage(chatId, chat.getLanguage()));
                    chat.setChatStage(ChatStage.YEAR_MAX);
                    return sendMessage(getYearQuestionMessage(chatId, chat.getLanguage(), chat.getChatStage() == ChatStage.YEAR_MIN));
                }
            }
        }
        return null;
    }

    private SendMessageDTO getLanguageChoiceMessage(Long chatId) {
        // prepare keyboard
        KeyboardButtonDTO[][] buttons = new KeyboardButtonDTO[2][];
        buttons[0] = new KeyboardButtonDTO[1];
        buttons[1] = new KeyboardButtonDTO[2];
        buttons[0][0] = new KeyboardButtonDTO(messageProvider.getMessage("language_az", null));
        buttons[1][0] = new KeyboardButtonDTO(messageProvider.getMessage("language_en", null));
        buttons[1][1] = new KeyboardButtonDTO(messageProvider.getMessage("language_ru", null));
        ReplyKeyboardMarkupDTO replyKeyboardMarkupDTO = new ReplyKeyboardMarkupDTO();
        replyKeyboardMarkupDTO.setKeyboardButtonArray(buttons);
        replyKeyboardMarkupDTO.setOneTimeKeyboard(true);

        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(messageProvider.getMessage("start_message", null));
        sendMessageDTO.setReplyKeyboard(replyKeyboardMarkupDTO);

        return sendMessageDTO;
    }

    private SendMessageDTO getMakeChoiceMessage(Long chatId, Language language) {
        int columnSize = 1;
        List<MakeEntity> makeList = makeService.getMakeList();
        int rowCount = makeList.size() / columnSize;
        KeyboardButtonDTO[][] buttons = new KeyboardButtonDTO[rowCount][];
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            int columnCount = columnSize;
            buttons[rowIndex] = new KeyboardButtonDTO[columnCount];
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                buttons[rowIndex][columnIndex] = new KeyboardButtonDTO(makeList.get(rowIndex * columnSize + columnIndex).getMake());
            }
        }
        ReplyKeyboardMarkupDTO replyKeyboardMarkupDTO = new ReplyKeyboardMarkupDTO();
        replyKeyboardMarkupDTO.setKeyboardButtonArray(buttons);
        replyKeyboardMarkupDTO.setOneTimeKeyboard(true);
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(messageProvider.getMessage("question_make_choice", language));
        sendMessageDTO.setReplyKeyboard(replyKeyboardMarkupDTO);
        return sendMessageDTO;
    }


    private SendMessageDTO getModelChoiceMessage(Long chatId, Language language, int makeId) {
        int columnSize = 1;
        List<ModelEntity> modelList = modelService.getModelList(makeId);
        int rowCount = modelList.size() / columnSize;
        KeyboardButtonDTO[][] buttons = new KeyboardButtonDTO[rowCount][];
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            int columnCount = columnSize;
            buttons[rowIndex] = new KeyboardButtonDTO[columnCount];
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                buttons[rowIndex][columnIndex] = new KeyboardButtonDTO(modelList.get(rowIndex * columnSize + columnIndex).getModel());
            }
        }
        ReplyKeyboardMarkupDTO replyKeyboardMarkupDTO = new ReplyKeyboardMarkupDTO();
        replyKeyboardMarkupDTO.setKeyboardButtonArray(buttons);
        replyKeyboardMarkupDTO.setOneTimeKeyboard(true);
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(messageProvider.getMessage("question_model_choice", language));
        sendMessageDTO.setReplyKeyboard(replyKeyboardMarkupDTO);
        return sendMessageDTO;
    }

    public void saveSpecialCarUpdateToDB(SpecificVehicleSearchParameter newSpecificVehicleSearchParameter, Long chatId) {
        Chat chat = chatDataService.getChatByChatId(chatId);
        newSpecificVehicleSearchParameter.setChat(chat);
        specificVehicleRepository.save(newSpecificVehicleSearchParameter);
    }


    private SendMessageDTO getDeleteMessage(Long chatId, Language language) {
        List<SearchParameter> searchParameterList = searchParameterService.getSearchParameter(chatId);
        KeyboardButtonDTO[][] buttons = new KeyboardButtonDTO[searchParameterList.size()][];
        for (int i = 0; i < searchParameterList.size(); i++) {
            buttons[i] = new KeyboardButtonDTO[1];
            for (int j = 0; j < 1; j++) {
                buttons[i][j] = new KeyboardButtonDTO(searchParameterList.get(i + j).getMake() + " " + searchParameterList.get(i + j).getModel());
            }
        }
        ReplyKeyboardMarkupDTO replyKeyboardMarkupDTO = new ReplyKeyboardMarkupDTO();
        replyKeyboardMarkupDTO.setKeyboardButtonArray(buttons);
        replyKeyboardMarkupDTO.setOneTimeKeyboard(true);
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(messageProvider.getMessage("question_delete_search", language));
        sendMessageDTO.setReplyKeyboard(replyKeyboardMarkupDTO);
        return sendMessageDTO;
    }

    private SendMessageDTO getSpecificDeleteMessage(Long chatId, Language language) {
        List<SpecificVehicleSearchParameter> searchParameterList = searchParameterService.getSpecificSearchParameter(chatId);
        KeyboardButtonDTO[][] buttons = new KeyboardButtonDTO[searchParameterList.size()][];
        for (int i = 0; i < searchParameterList.size(); i++) {
            buttons[i] = new KeyboardButtonDTO[1];
            for (int j = 0; j < 1; j++) {
                buttons[i][j] = new KeyboardButtonDTO(searchParameterList.get(i + j).getGeneralInfo());
            }
        }
        ReplyKeyboardMarkupDTO replyKeyboardMarkupDTO = new ReplyKeyboardMarkupDTO();
        replyKeyboardMarkupDTO.setKeyboardButtonArray(buttons);
        replyKeyboardMarkupDTO.setOneTimeKeyboard(true);
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(messageProvider.getMessage("question_delete_search", language));
        sendMessageDTO.setReplyKeyboard(replyKeyboardMarkupDTO);
        return sendMessageDTO;
    }

    private SendMessageDTO getSpecificAddMessage(Long chatId, Language language, SpecificVehicleSearchParameter newSpecificVehicleSearchParameter) {
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setText(messageProvider.getMessage("new_specific_info", language) + "\n" +
                newSpecificVehicleSearchParameter.getGeneralInfo());
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setReplyKeyboard(new ReplyKeyboardRemoveDTO(true));
        return sendMessageDTO;
    }

    private SendMessageDTO getLanguageMessage(Long chatId, Language language) {
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setText(messageProvider.getMessage("language_choice", language));
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setReplyKeyboard(new ReplyKeyboardRemoveDTO(true));
        return sendMessageDTO;
    }

    private SendMessageDTO getAlreadyExistisMessage(Long chatId, Language language) {
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setText(messageProvider.getMessage("already_exists_info", language));
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setReplyKeyboard(new ReplyKeyboardRemoveDTO(true));
        return sendMessageDTO;
    }

    private SendMessageDTO getPriceQuestionMessage(Long chatId, Language language, boolean isLowPriceQuestion) {
        SendMessageDTO sendMessageDTO = getSkipableQuestion(language);
        sendMessageDTO.setChatId(chatId);
        if (isLowPriceQuestion == true)
            sendMessageDTO.setText(messageProvider.getMessage("question_min_price", language));
        else
            sendMessageDTO.setText(messageProvider.getMessage("question_max_price", language));
        return sendMessageDTO;
    }

    private SendMessageDTO getAllSearchMessage(Long chatId, String text) {
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(text);
        sendMessageDTO.setReplyKeyboard(new ReplyKeyboardRemoveDTO(true));
        return sendMessageDTO;
    }

    private SendMessageDTO getYearQuestionMessage(Long chatId, Language language, boolean isLowYearQuestion) {
        SendMessageDTO sendMessageDTO = getSkipableQuestion(language);
        sendMessageDTO.setChatId(chatId);
        if (isLowYearQuestion == true)
            sendMessageDTO.setText(messageProvider.getMessage("question_min_year", language));
        else
            sendMessageDTO.setText(messageProvider.getMessage("question_max_year", language));
        return sendMessageDTO;
    }

    private SendMessageDTO getSkipableQuestion(Language language) {
        KeyboardButtonDTO[][] buttons = new KeyboardButtonDTO[1][1];
        buttons[0][0] = new KeyboardButtonDTO(messageProvider.getMessage("skip_button", language));
        ReplyKeyboardMarkupDTO replyKeyboard = new ReplyKeyboardMarkupDTO();
        replyKeyboard.setOneTimeKeyboard(true);
        replyKeyboard.setKeyboardButtonArray(buttons);

        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setReplyKeyboard(replyKeyboard);
        return sendMessageDTO;
    }

    public SendMessageDTO getNewCarMessage(Long chatId, String text) {
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(text);
        sendMessageDTO.setReplyKeyboard(new ReplyKeyboardRemoveDTO(true));
        return sendMessageDTO;
    }

    public SendMessageDTO getNoAnyCarFoundMessage(Long chatId, Language language, String carInfo) {
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(messageProvider.getMessage("no_any_special_car_info", language) + carInfo);
        sendMessageDTO.setReplyKeyboard(new ReplyKeyboardRemoveDTO(true));
        return sendMessageDTO;
    }

    public SendMessageDTO notFoundAnySpecialCar(Long chatId, Language language) {
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(messageProvider.getMessage("car_not_found", language));
        sendMessageDTO.setReplyKeyboard(new ReplyKeyboardRemoveDTO(true));
        return sendMessageDTO;
    }

    public SendMessageDTO getChangedSpecificCarMessage(Long chatId,
                                                       SpecificVehicleSearchParameter newSpecificVehicleSearchParameter,
                                                       SpecificVehicleSearchParameter oldSpecificVehicleSearchParameter,
                                                       Language language) {
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(
                messageProvider.getMessage("special_change_from_info", language) + "\n" + "" + "\n"
                        + oldSpecificVehicleSearchParameter.toString() + "\n"
                        + messageProvider.getMessage("special_change_to_info", language) + "\n" + "" + "\n"
                        + newSpecificVehicleSearchParameter.toString()
        );
        sendMessageDTO.setReplyKeyboard(new ReplyKeyboardRemoveDTO(true));
        return sendMessageDTO;
    }

    private SendMessageDTO getInvalidNumberErrorMessage(Long chatId, Language language) {
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(messageProvider.getMessage("invalid_number_info", language));
        sendMessageDTO.setReplyKeyboard(new ReplyKeyboardRemoveDTO(true));
        return sendMessageDTO;
    }


    private SendMessageDTO getSpecificInfoMessage(Long chatId, Language language) {
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setText(messageProvider.getMessage("specific_info", language));
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setReplyKeyboard(new ReplyKeyboardRemoveDTO(true));
        return sendMessageDTO;
    }

    private SendMessageDTO getSearchParametersFinishMessage(Long chatId, Language language, SearchParameter searchParameter) {
        String make = searchParameter.getMake();
        String model = searchParameter.getModel();
        String minPrice = (searchParameter.getMinPrice() != null) ? searchParameter.getMinPrice().toString() : messageProvider.getMessage("notification.no_entered", language);
        String maxPrice = (searchParameter.getMaxPrice() != null) ? searchParameter.getMaxPrice().toString() : messageProvider.getMessage("notification.no_entered", language);
        String minYear = (searchParameter.getMinYear() != null) ? searchParameter.getMinYear().toString() : messageProvider.getMessage("notification.no_entered", language);
        String maxYear = (searchParameter.getMaxYear() != null) ? searchParameter.getMaxYear().toString() : messageProvider.getMessage("notification.no_entered", language);

        String text = messageProvider.getMessage("entered_search_params", language) + ": \n" +
                "- " + messageProvider.getMessage("notification.make", language) + ": " + make + "\n" +
                "- " + messageProvider.getMessage("notification.model", language) + ": " + model + "\n" +
                "- " + messageProvider.getMessage("notification.min_price", language) + ": " + minPrice + "\n" +
                "- " + messageProvider.getMessage("notification.max_price", language) + ": " + maxPrice + "\n" +
                "- " + messageProvider.getMessage("notification.max_year", language) + ": " + maxYear + "\n" +
                "- " + messageProvider.getMessage("notification.min_year", language) + ": " + minYear + "\n";

        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(text);
        sendMessageDTO.setReplyKeyboard(new ReplyKeyboardRemoveDTO(true));
        return sendMessageDTO;
    }

}
