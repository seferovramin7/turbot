package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.Chat;
import com.turboparser.turbo.entity.SearchParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchParameterRepository extends JpaRepository<SearchParameter, Long> {

    @Query("select s from SearchParameter s where s.chat.chatId = :chatId")
    SearchParameter getSearchParameterByChatId(@Param(("chatId")) Long chatId);

    @Query("select s.chat from SearchParameter s " +
            "where s.chat.chatStage = com.turboparser.turbo.constant.ChatStage.READY_RECEIVED " +
            "and " +
            "s.make = ?1 " +
            "and ((s.minPrice is null and ?2 >= 0L) or (s.minPrice is not null and ?2 >= s.minPrice)) " +
            "and ((s.maxPrice is null and ?2 <= 1000000L) or (s.maxPrice is not null and ?2 <= s.maxPrice)) ")
    List<Chat> getChatListBySearchParameters( @Param("price") Long price,
                                             @Param("number_of_room") Long numberOfRoom);

}
