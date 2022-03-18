package com.turboparser.turbo.service.impl;

import com.turboparser.turbo.entity.SearchParameter;
import com.turboparser.turbo.entity.SpecificVehicleSearchParameter;
import com.turboparser.turbo.repository.SearchParameterRepository;
import com.turboparser.turbo.repository.SpecificVehicleRepository;
import com.turboparser.turbo.service.SearchParameterService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class SearchParameterServiceImpl implements SearchParameterService {

    private final SearchParameterRepository searchParameterRepository;
    private final SpecificVehicleRepository specificVehicleRepository;

    public SearchParameterServiceImpl(SearchParameterRepository searchParameterRepository, SpecificVehicleRepository specificVehicleRepository) {
        this.searchParameterRepository = searchParameterRepository;
        this.specificVehicleRepository = specificVehicleRepository;
    }

    @Override
    public SearchParameter getSearchParameterByMaxMessageId(Long chatId) {
        return searchParameterRepository.findFirstByChat_ChatIdOrderByMessageIdDesc(chatId);
    }

    @Override
    public List<SearchParameter> getSearchParameter(Long chatId) {
        return searchParameterRepository.findAllByChat_ChatId(chatId);
    }

    @Override
    public SearchParameter saveSearchParameter(SearchParameter searchParameter) {
        return searchParameterRepository.save(searchParameter);
    }

    @Override
    public SearchParameter updateSearchParameter(SearchParameter searchParameter) {
        return searchParameterRepository.save(searchParameter);
    }

    @Override
    public void deleteSearchParameter(Long chatId) {
        SearchParameter searchParameter = searchParameterRepository.getSearchParameterByChatId(chatId);
        if (searchParameter != null)
            searchParameterRepository.delete(searchParameter);
    }

    @Override
    @Transactional
    public void deleteAllByModel(String model){
        searchParameterRepository.deleteAllByModel(model);
    }

    @Override
    public void deleteSearchParameterByMakeAndModel(Long chatId, String make, String model) {
        SearchParameter searchParameter = searchParameterRepository.findByMakeAndModelAndChat_ChatId( make, model, chatId);
        System.out.println("searchParameter" + searchParameter);
        if (searchParameter != null)
            searchParameterRepository.delete(searchParameter);
    }

    @Override
    public void deleteSpecialSearchParameterByLotId(Long chatId, Long lotId) {
        SpecificVehicleSearchParameter byChat_chatIdAndLotId = specificVehicleRepository.findByChat_ChatIdAndLotId(chatId, lotId);
        System.out.println("searchParameter" + byChat_chatIdAndLotId);
        if (byChat_chatIdAndLotId != null)
            specificVehicleRepository.delete(byChat_chatIdAndLotId);
    }

}
