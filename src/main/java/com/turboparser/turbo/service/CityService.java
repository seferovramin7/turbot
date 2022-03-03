package com.turboparser.turbo.service;

import com.turboparser.turbo.entity.City;

import java.io.IOException;
import java.util.List;

public interface CityService {

    void updateCities() throws IOException;

    List<City> getCityList();

    City getCityByCityName(String cityName);

}
