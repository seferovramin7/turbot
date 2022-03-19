package com.turboparser.turbo.service;

import com.turboparser.turbo.entity.Chat;
import com.turboparser.turbo.entity.SearchParameter;
import com.turboparser.turbo.entity.SpecificVehicleSearchParameter;

import java.util.List;

public interface SearchParameterService {

    SearchParameter getSearchParameterByMaxMessageId(Long chatId);

    List<SearchParameter> getSearchParameter(Long chatId);

    List<SpecificVehicleSearchParameter> getSpecificSearchParameter(Long chatId);


    SearchParameter saveSearchParameter(SearchParameter searchParameter);

    SearchParameter updateSearchParameter(SearchParameter searchParameter);

    void deleteSearchParameterByMakeAndModel(Long chatId, String make,String model);

    void deleteSearchParameter(Long chatId);

    void deleteAllByModel(String model);

    void deleteSpecialSearchParameterByLotId(Long chatId, Long lotId);


}
