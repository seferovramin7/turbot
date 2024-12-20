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
    public List<SpecificVehicleSearchParameter> getSpecificSearchParameter(Long chatId) {
        return specificVehicleRepository.findAllByChat_ChatId(chatId);
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
    @Transactional
    public void deleteAllByModel(String model) {
        searchParameterRepository.deleteAllByModel(model);
    }

    @Override
    public void deleteSearchParameterByMakeAndModelAndMinAndMaxPrice(Long chatId,
                                                                     String make,
                                                                     String model,
                                                                     Long minPrice,
                                                                     Long maxPrice) {
        SearchParameter searchParameter = searchParameterRepository.findByMakeAndModelAndMinPriceAndMaxPriceAndChat_ChatId(
                make,
                model,
                minPrice,
                maxPrice,
                chatId);

        if (searchParameter != null)
            searchParameterRepository.delete(searchParameter);
    }


}
