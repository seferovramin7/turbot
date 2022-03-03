package com.turboparser.turbo.service;

import com.turboparser.turbo.entity.Home;

import java.io.IOException;
import java.util.List;

public interface HomeService {

    void findNewHomes() throws IOException;

    List<Home> getUnsentHomes();

    Home updateHome(Home home);

}
