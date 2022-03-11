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
import com.turboparser.turbo.model.CarType;
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
    @Value("${telegram.api.base-url}")
    private String telegramApiBaseUrl;
    @Value("${telegram.api.token}")
    private String botToken;
    @Value("${telegram.bot.name}")
    private String botName;
    private Long offset = null;
    private  final RequestCreationService requestCreationService;
    private  final CarTypeMapper carTypeMapper;


    public TelegramMessagingServiceImpl(HttpRequestService httpRequestService, ChatDataService chatDataService,
                                        MessageProvider messageProvider,
                                        MakeService makeService, ModelService modelService, SearchParameterService searchParameterService, RequestCreationService requestCreationService, CarTypeMapper carTypeMapper) {
        this.httpRequestService = httpRequestService;
        this.chatDataService = chatDataService;
        this.messageProvider = messageProvider;
        this.makeService = makeService;
        this.modelService = modelService;
        this.searchParameterService = searchParameterService;
        this.requestCreationService = requestCreationService;
        this.carTypeMapper = carTypeMapper;
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
    public SendMessageResponseDTO reply(TelegramUpdateDTO telegramUpdateDTO) {

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
        Chat chat = chatDataService.getChatByChatId(chatId);

        if (chat.getChatStage() == ChatStage.START || text.equals("/reset") || text.equals("/about")) {

            if (text.equals("/reset")) {
                chat.setChatStage(ChatStage.START);
                chat = chatDataService.updateChat(chat);
                searchParameterService.deleteSearchParameter(chatId);
                sendMessage(getResetInfoMessage(chatId, chat.getLanguage()));
            }

            if (!(text.equals("azərbaycanca") || text.equals("русский") || text.equals("english"))) {
                return sendMessage(getLanguageChoiceMessage(chatId));
            } else {
                if (text.equals("azərbaycanca")) chat.setLanguage(Language.az);
                else if (text.equals("english")) chat.setLanguage(Language.en);
                else chat.setLanguage(Language.ru);
                chat.setChatStage(ChatStage.CAR_MAKE);
                chat = chatDataService.updateChat(chat);
                return sendMessage(getMakeChoiceMessage(chatId, chat.getLanguage()));
            }
        }
        // Make select
        else if (chat.getChatStage() == ChatStage.CAR_MAKE) {
            MakeEntity make = makeService.getMakeByMakeName(text);
            int makeId = make.getMakeId();
            if (make != null) {
                SearchParameter searchParameter = new SearchParameter();
                searchParameter.setChat(chat);
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
            // check if this parameter was skipped
            if (text.equals(messageProvider.getMessage("skip_button", chat.getLanguage()))) {
                // do nothing
            } else {
                String model = "";
                model = text;
                SearchParameter searchParameter = searchParameterService.getSearchParameter(chatId);
                searchParameter.setModel(model);
                searchParameterService.updateSearchParameter(searchParameter);
            }
            chat.setChatStage(ChatStage.PRICE_MIN);
            chatDataService.updateChat(chat);
            return sendMessage(getPriceQuestionMessage(chatId, chat.getLanguage(), chat.getChatStage() == ChatStage.PRICE_MIN));
        } else if (chat.getChatStage() == ChatStage.PRICE_MIN || chat.getChatStage() == ChatStage.PRICE_MAX) {
            // check if this parameter was skipped
            if (text.equals(messageProvider.getMessage("skip_button", chat.getLanguage()))) {
                // do nothing
            } else {
                Long enteredPrice = null;
                try {
                    enteredPrice = Long.parseLong(text);
                } catch (NumberFormatException ex) {
                    log.error("Incorrect price. Entered value: " + enteredPrice);
                    sendMessage(getInvalidNumberErrorMessage(chatId, chat.getLanguage()));
                    return sendMessage(getPriceQuestionMessage(chatId, chat.getLanguage(), chat.getChatStage() == ChatStage.PRICE_MIN));
                }
                SearchParameter searchParameter = searchParameterService.getSearchParameter(chatId);
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
                // do nothing
            } else {
                Long enteredNumber = null;
                try {
                    SearchParameter searchParameter = searchParameterService.getSearchParameter(chatId);
                    if (chat.getChatStage() == ChatStage.YEAR_MIN) {
                        enteredNumber = Long.parseLong(text);
                        searchParameter.setMinYear(enteredNumber);
                        searchParameterService.updateSearchParameter(searchParameter);
                        chat.setChatStage(ChatStage.YEAR_MAX);
                        chatDataService.updateChat(chat);
                        return sendMessage(getYearQuestionMessage(chatId, chat.getLanguage(), true));
                    } else {
                        chat.setChatStage(ChatStage.YEAR_MAX);
                        enteredNumber = Long.parseLong(text);
                        searchParameter.setMaxYear(enteredNumber);
                        chatDataService.updateChat(chat);
                        searchParameterService.updateSearchParameter(searchParameter);
//                        return sendMessage(getYearQuestionMessage(chatId, chat.getLanguage(), true));
                    }
                    searchParameter = searchParameterService.getSearchParameter(chatId);
                    searchParameter = searchParameterService.updateSearchParameter(searchParameter);
                    chat.setChatStage(ChatStage.READY_RECEIVED);
                    chat = chatDataService.updateChat(chat);
                    if (searchParameter == null)
                        searchParameter = searchParameterService.getSearchParameter(chatId);
                    sendMessage(getSearchParametersFinishMessage(chatId, chat.getLanguage(), searchParameter));

                    System.out.println("SEARCH : " + searchParameter);
//                    requestCreationService.createRequest(searchParameter);
                    return sendMessage(getReadyInfoMessage(chatId, chat.getLanguage()));

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
        int columnSize = 3;
        List<MakeEntity> makeList = makeService.getMakeList();
        int rowCount = (makeList.size() % columnSize == 0) ? makeList.size() / columnSize : makeList.size() / columnSize + 1;
        KeyboardButtonDTO[][] buttons = new KeyboardButtonDTO[rowCount][];
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            int columnCount = columnSize;
            if (rowIndex == rowCount - 1 && makeList.size() % columnSize != 0) {
                columnCount = makeList.size() % columnSize;
            }
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
        int columnSize = 3;
        List<ModelEntity> modelList = modelService.getModelList(makeId);

        int rowCount = (modelList.size() % columnSize == 0) ? modelList.size() / columnSize : modelList.size() / columnSize + 1;
        KeyboardButtonDTO[][] buttons = new KeyboardButtonDTO[rowCount][];
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            int columnCount = columnSize;
            if (rowIndex == rowCount - 1 && modelList.size() % columnSize != 0) {
                columnCount = modelList.size() % columnSize;
            }
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


    private SendMessageDTO getPriceQuestionMessage(Long chatId, Language language, boolean isLowPriceQuestion) {
        SendMessageDTO sendMessageDTO = getSkipableQuestion(language);
        sendMessageDTO.setChatId(chatId);
        if (isLowPriceQuestion == true)
            sendMessageDTO.setText(messageProvider.getMessage("question_min_price", language));
        else
            sendMessageDTO.setText(messageProvider.getMessage("question_max_price", language));
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

    private SendMessageDTO getReadyInfoMessage(Long chatId, Language language) {
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(messageProvider.getMessage("ready_info", language));
        sendMessageDTO.setReplyKeyboard(new ReplyKeyboardRemoveDTO(true));
        return sendMessageDTO;
    }

    public SendMessageDTO getNewCarMessage(Long chatId, String text) {
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(text);
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

    private SendMessageDTO getResetInfoMessage(Long chatId, Language language) {
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setText(messageProvider.getMessage("reset_info", language));
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
