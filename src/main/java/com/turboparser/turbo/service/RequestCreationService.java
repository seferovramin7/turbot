package com.turboparser.turbo.service;


import com.turboparser.turbo.model.CarType;
import com.turboparser.turbo.util.CarTypeMapper;
import com.turboparser.turbo.util.URLcreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RequestCreationService {

    @Autowired
    CarTypeMapper carTypeMapper;

    @Autowired
    URLcreator urLcreator;

    @Autowired
    RestService restService;

    @Scheduled(fixedRate = 900000)
    public void createRequest() throws IOException {

        CarType carType = carTypeMapper.buildCar("2", "88", "0", "100000", "2015",
                "2016", "2300", "2300", "0", "150000",
                "1", "2");

        CarType emptyCarType = carTypeMapper.buildCar("2", "", "", "", "",
                "", "", "", "", "",
                "", "");

//        String url = urLcreator.createUrl(carType);

        String url = urLcreator.createUrl(emptyCarType);

        restService.makeAndModelRestService(url);
//        restService.generalRestService(url);
    }

//    @Scheduled(fixedRate = 900000)
    public void updateMakeAndModelDB() throws IOException {
        restService.makeAndModelRestService("https://turbo.az/");
    }

}
