package com.turboparser.turbo.service;

import com.turboparser.turbo.dto.HomeDTO;
import com.turboparser.turbo.entity.City;
import com.turboparser.turbo.entity.Home;

import java.util.List;

public interface DTOToModelConvService {

    Home getHome(HomeDTO homeDTO, List<City> cityList);

}
