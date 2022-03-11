package com.turboparser.turbo.util;

import com.turboparser.turbo.entity.CarTypeEntity;
import com.turboparser.turbo.entity.MakeEntity;
import com.turboparser.turbo.entity.ModelEntity;
import com.turboparser.turbo.entity.VehicleArchive;
import com.turboparser.turbo.repository.CarTypeRepository;
import com.turboparser.turbo.repository.TurboMakeRepository;
import com.turboparser.turbo.repository.TurboModelRepository;
import com.turboparser.turbo.repository.VehicleArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DBactions {

    @Autowired
    VehicleArchiveRepository archiveRepository;

    @Autowired
    CarTypeRepository carTypeRepository;

    @Autowired
    TurboMakeRepository turboMakeRepository;

    @Autowired
    TurboModelRepository turboModelRepository;

    public void insertOrIgnoreDB(String lotLink, String carPriceTotal) {
        if (archiveRepository.getByLotAndPrice(lotLink, carPriceTotal) == null) {
            archiveRepository.save(VehicleArchive.builder().lot(lotLink).price(carPriceTotal).build());
        }
    }

    public void insertVehicleType(CarTypeEntity carTypeEntity) {
        carTypeRepository.save(carTypeEntity);
    }

    public List<CarTypeEntity> getAllVehicleTypes() {
        return carTypeRepository.findAll();
    }

    public void updateMakeTable(MakeEntity makeEntity) {
        if (turboMakeRepository.getByMakeAndMakeId(makeEntity.getMake(), makeEntity.getMakeId()) == null) {
            turboMakeRepository.save(makeEntity);
        }
    }

    public void updateModelTable(ModelEntity modelEntity) {
        if (turboModelRepository.getByModelAndModelIdAndMakeId(modelEntity.getModel(), modelEntity.getModelId(), modelEntity.getMakeId()) == null) {
            turboModelRepository.save(modelEntity);
        }
    }
}
