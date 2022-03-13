package com.turboparser.turbo.service.impl;

import com.turboparser.turbo.entity.SearchParameter;
import com.turboparser.turbo.repository.SearchParameterRepository;
import com.turboparser.turbo.service.SearchParameterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchParameterServiceImpl implements SearchParameterService {

    private final SearchParameterRepository searchParameterRepository;

    public SearchParameterServiceImpl(SearchParameterRepository searchParameterRepository) {
        this.searchParameterRepository = searchParameterRepository;
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

}
