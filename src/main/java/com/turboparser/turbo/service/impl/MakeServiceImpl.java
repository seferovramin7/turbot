package com.turboparser.turbo.service.impl;

import com.turboparser.turbo.entity.City;
import com.turboparser.turbo.entity.MakeEntity;
import com.turboparser.turbo.repository.CityRepository;
import com.turboparser.turbo.repository.TurboMakeRepository;
import com.turboparser.turbo.service.CityService;
import com.turboparser.turbo.service.MakeService;
import com.turboparser.turbo.service.ScrapService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
public class MakeServiceImpl implements MakeService {

    private final TurboMakeRepository makeRepository;
    private final ScrapService scrapService;

    public MakeServiceImpl(TurboMakeRepository makeRepository, ScrapService scrapService) {
        this.makeRepository = makeRepository;
        this.scrapService = scrapService;
    }

    @Transactional
    @Override
    public void updateMakes() throws IOException {
        List<String> cityList = scrapService.getCities();
        long numberOfCity = makeRepository.countCities();
        if (numberOfCity > 0) {
            makeRepository.deactiveCities();
            cityList.stream().forEach(item -> {
                City city = makeRepository.getCityByDescription(item);
                if (city != null) {
                    city.setActive(true);
                }
                else {
                    city = new City();
                    city.setDescription(item);
                }
                makeRepository.save(city);
            });
        }
        else {
            cityList.stream().forEach(item -> {
                MakeEntity city = new MakeEntity();
                city.setDescription(item);
                city.setActive(true);
                makeRepository.save(city);
            });
        }
    }

    @Override
    public List<MakeEntity> getMakeList() {
        return makeRepository.getCities();
    }

    @Override
    public MakeEntity getMakeByMakeName(String makeName) {
        return makeRepository.getCityByDescription(makeName);
    }
}
