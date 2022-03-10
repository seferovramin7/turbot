package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.Chat;

import java.util.List;

public interface SearchParameterCustomRepository {

    List<Chat> getChatListBySearchedParameters( Long price, Long numberOfRooms);

}
