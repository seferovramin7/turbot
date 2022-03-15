package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.Chat;
import com.turboparser.turbo.entity.SearchParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchParameterRepository extends JpaRepository<SearchParameter, Long> {


    List<SearchParameter> getAllByChat_ChatId(Long chatId);

    @Query("select s from SearchParameter s where s.chat.chatId = :chatId")
    SearchParameter getSearchParameterByChatId(@Param(("chatId")) Long chatId);

    SearchParameter findByMakeAndModelAndChat_ChatId(String make, String model, Long id);

    SearchParameter findFirstByChat_ChatIdOrderByMessageIdDesc(Long chatId);

    List<SearchParameter> findAllByChat_ChatId(Long chatId);

    List<SearchParameter> deleteAllByModel(String model);

}
