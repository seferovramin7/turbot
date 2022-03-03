package com.turboparser.turbo.dto.telegram.send.text;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import com.turboparser.turbo.dto.telegram.send.ReplyKeyboard;

@Data
public class SendMessageDTO {

    @JsonProperty("chat_id")
    private Long chatId;

    @JsonProperty("text")
    private String text;

    @JsonProperty("reply_markup")
    private ReplyKeyboard replyKeyboard;

}
