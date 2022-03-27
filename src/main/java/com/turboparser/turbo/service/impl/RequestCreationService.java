package com.turboparser.turbo.service.impl;


import com.turboparser.turbo.constant.Currency;
import com.turboparser.turbo.dto.telegram.send.text.NotificationDTO;
import com.turboparser.turbo.entity.MakeEntity;
import com.turboparser.turbo.entity.ModelEntity;
import com.turboparser.turbo.entity.SearchParameter;
import com.turboparser.turbo.entity.SpecificVehicleSearchParameter;
import com.turboparser.turbo.model.CarType;
import com.turboparser.turbo.repository.TurboMakeRepository;
import com.turboparser.turbo.repository.TurboModelRepository;
import com.turboparser.turbo.util.CarTypeMapper;
import com.turboparser.turbo.util.URLcreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

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

    @Value("${azn_fx_rate}")
    private String azn;

    @Value("${usd_fx_rate}")
    private String usd;

    @Value("${euro_fx_rate}")
    private String euro;

    static float getMultiplication(float multiplication, Currency currency, String azn, String euro, String usd, SearchParameter searchParameter) {
        switch (currency) {
            case AZN:
                multiplication = Float.parseFloat(azn);
                break;
            case EUR:
                multiplication = Float.parseFloat(euro);
                break;
            case USD:
                multiplication = Float.parseFloat(usd);
                break;
        }
        System.out.println("multiplication" + multiplication);
        return multiplication;
    }

    public List<NotificationDTO> createRequest(SearchParameter searchParameter) throws IOException, ParseException {
        if (searchParameter.getChat().getReqLimit() != null || searchParameter.getChat().getReqLimit() > 0) {
            MakeEntity byMake = turboMakeRepository.getByMake(searchParameter.getMake());
            String make = String.valueOf(byMake.getMakeId());
            ModelEntity byModel = turboModelRepository.getByModel(searchParameter.getModel());
            String model = String.valueOf(byModel.getModelId());
            float multiplication = 1;
            multiplication = getMultiplication(multiplication, searchParameter.getCurrency(), azn, euro, usd, searchParameter);

            CarType emptyCarType = carTypeMapper.buildCar(make,
                    Objects.toString(model, ""),
                    Objects.toString(searchParameter.getMinPrice() * multiplication, ""),
                    Objects.toString(searchParameter.getMaxPrice() * multiplication, ""),
                    Objects.toString(searchParameter.getMinYear(), ""),
                    Objects.toString(searchParameter.getMaxYear(), ""),
                    "", "",
                    "", "",
                    "", "");
            String url = urLcreator.createUrl(emptyCarType);
            try {
                List<NotificationDTO> notificationDTOList = restService.generalRestService(url);
                return notificationDTOList;
            } catch (NullPointerException e) {
                return null;
            }
        } else {
        }
        return null;
    }

    public SpecificVehicleSearchParameter createSpecificRequest(String link) throws IOException, ParseException {
        SpecificVehicleSearchParameter specificVehicleSearchParameter = restService.specificRestService("https://turbo.az/autos/" + link);
        return specificVehicleSearchParameter;
    }


    @Scheduled(fixedRate = 86400000)
    public void updateMakeAndModelDB() throws IOException {
        restService.makeAndModelRestService("https://turbo.az/");
    }
}
