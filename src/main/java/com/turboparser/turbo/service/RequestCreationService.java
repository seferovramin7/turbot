package com.turboparser.turbo.service;


import com.turboparser.turbo.dto.telegram.send.text.NotificationDTO;
import com.turboparser.turbo.entity.MakeEntity;
import com.turboparser.turbo.entity.ModelEntity;
import com.turboparser.turbo.entity.SearchParameter;
import com.turboparser.turbo.model.CarType;
import com.turboparser.turbo.repository.TurboMakeRepository;
import com.turboparser.turbo.repository.TurboModelRepository;
import com.turboparser.turbo.util.CarTypeMapper;
import com.turboparser.turbo.util.URLcreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
public class RequestCreationService {

    @Autowired
    CarTypeMapper carTypeMapper;

    @Autowired
    URLcreator urLcreator;

    @Autowired
    RestService restService;

    @Autowired
    TurboMakeRepository turboMakeRepository;

    @Autowired
    TurboModelRepository turboModelRepository;


//    @Scheduled(fixedRate = 900000)
    public void createRequest() throws IOException, ParseException {
        CarType carType = carTypeMapper.buildCar("2", "88", "0", "100000", "2015",
                "2016", "2300", "2300", "0", "150000",
                "1", "2");


        CarType prius = carTypeMapper.buildCar("1", "", "", "", "",
                "", "", "", "", "",
                "", "");


        String url = urLcreator.createUrl(prius);
        List<NotificationDTO> notificationDTOList = restService.generalRestService(url);
        System.out.println(notificationDTOList);
    }

    public List<NotificationDTO> createRequest(SearchParameter searchParameter) throws IOException, ParseException {
        MakeEntity byMake = turboMakeRepository.getByMake(searchParameter.getMake());
        String make = String.valueOf(byMake.getMakeId());
        ModelEntity byModel = turboModelRepository.getByModel(searchParameter.getModel());
        String model = String.valueOf(byModel.getModelId());

        CarType emptyCarType = carTypeMapper.buildCar(make, model,
                searchParameter.getMinPrice().toString(),
                searchParameter.getMaxPrice().toString(),
                searchParameter.getMinYear().toString(),
                searchParameter.getMaxYear().toString(),
                "", "",
                "", "",
                "", "");
        String url = urLcreator.createUrl(emptyCarType);
        List<NotificationDTO> notificationDTOList = restService.generalRestService(url);
        return notificationDTOList;
    }

    @Scheduled(fixedRate = 900000)
    public void updateMakeAndModelDB() throws IOException {
        restService.makeAndModelRestService("https://turbo.az/");
    }
}
