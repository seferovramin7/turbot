package com.turboparser.turbo.service.impl;

import com.turboparser.turbo.dto.HomeDTO;
import com.turboparser.turbo.entity.City;
import com.turboparser.turbo.entity.Home;
import com.turboparser.turbo.repository.HomeRepository;
import com.turboparser.turbo.service.CityService;
import com.turboparser.turbo.service.DTOToModelConvService;
import com.turboparser.turbo.service.HomeService;
import com.turboparser.turbo.service.ScrapService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    private final HomeRepository homeRepository;
    private final ScrapService scrapService;
    private final DTOToModelConvService dtoToModelConvService;
    private final CityService cityService;

    public HomeServiceImpl(HomeRepository homeRepository, ScrapService scrapService,
                           DTOToModelConvService dtoToModelConvService, CityService cityService) {
        this.homeRepository = homeRepository;
        this.scrapService = scrapService;
        this.dtoToModelConvService = dtoToModelConvService;
        this.cityService = cityService;
    }


    @Override
    public void findNewHomes() throws IOException {
        List<String> pageLinks = homeRepository.getPageLinks();
        List<HomeDTO> homeDTOList = scrapService.getHomes(pageLinks);
        if (homeDTOList.size() > 0) {
            List<City> cityList = cityService.getCityList();
            homeDTOList.stream().forEach(item -> {
                    Home home = dtoToModelConvService.getHome(item, cityList);
                    home.setAlreadySent(false);
                    homeRepository.save(home);
            });
        }
    }

    @Override
    public List<Home> getUnsentHomes() {
        return homeRepository.getUnsentHome();
    }

    @Override
    public Home updateHome(Home home) {
        return homeRepository.save(home);
    }

}
