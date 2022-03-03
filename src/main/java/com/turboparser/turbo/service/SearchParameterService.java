package com.turboparser.turbo.service;

import com.turboparser.turbo.entity.Chat;
import com.turboparser.turbo.entity.Home;
import com.turboparser.turbo.entity.SearchParameter;

import java.util.List;

public interface SearchParameterService {

    SearchParameter getSearchParameter(Long chatId);

    SearchParameter saveSearchParameter(SearchParameter searchParameter);

    SearchParameter updateSearchParameter(SearchParameter searchParameter);

    void deleteSearchParameter(Long chatId);

    List<Chat> getChatListByAppropriateParameters(Home home);

}
