package com.turboparser.turbo.service;

import com.turboparser.turbo.dto.HomeDTO;

import java.io.IOException;
import java.util.List;

public interface ScrapService {

    List<String> getCities() throws IOException;

    List<HomeDTO> getHomes(List<String> homeLinks) throws IOException;

}
