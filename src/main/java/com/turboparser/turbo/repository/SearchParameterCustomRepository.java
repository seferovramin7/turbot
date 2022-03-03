package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.Chat;
import com.turboparser.turbo.entity.City;

import java.util.List;

public interface SearchParameterCustomRepository {

    List<Chat> getChatListBySearchedParameters(City city, Long price, Long numberOfRooms);

}
