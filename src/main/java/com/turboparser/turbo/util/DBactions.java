package com.turboparser.turbo.util;

import com.turboparser.turbo.entity.CarTypeEntity;
import com.turboparser.turbo.entity.VehicleArchive;
import com.turboparser.turbo.repository.CarTypeRepository;
import com.turboparser.turbo.repository.VehicleArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DBactions {

    @Autowired
    VehicleArchiveRepository archiveRepository;

    @Autowired
    CarTypeRepository carTypeRepository;

    public void insertOrIgnoreDB(String lotLink, String carPriceTotal) {
        if (archiveRepository.getByLotAndPrice(lotLink, carPriceTotal) == null) {
            archiveRepository.save(VehicleArchive.builder().lot(lotLink).price(carPriceTotal).build());
        }
    }

    public void insertVehicleType(CarTypeEntity carTypeEntity){
        carTypeRepository.save(carTypeEntity);
    }

}
