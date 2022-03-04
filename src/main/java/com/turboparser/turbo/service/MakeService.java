package com.turboparser.turbo.service;

import com.turboparser.turbo.entity.City;
import com.turboparser.turbo.entity.MakeEntity;

import java.io.IOException;
import java.util.List;

public interface MakeService {

    void updateMakes() throws IOException;

    List<MakeEntity> getMakeList();

    MakeEntity getMakeByMakeName(String makeName);

}
