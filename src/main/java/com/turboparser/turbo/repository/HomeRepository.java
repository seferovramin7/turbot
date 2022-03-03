package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HomeRepository extends JpaRepository<Home, Long> {

    Home getHomeByLink(String pageLink);

    @Query("select h.link from Home h")
    List<String> getPageLinks();

    @Query("select h from Home h where h.alreadySent = false")
    List<Home> getUnsentHome();

}
