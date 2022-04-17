package com.turboparser.turbo.service.impl;

import com.turboparser.turbo.entity.ModelEntity;
import com.turboparser.turbo.repository.TurboModelRepository;
import com.turboparser.turbo.service.ModelService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
public class ModelServiceImpl implements ModelService {

    private final TurboModelRepository modelRepository;

    public ModelServiceImpl(TurboModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }


    @Transactional
    @Override
    public void updateModels() throws IOException {
//        List<String> cityList = scrapService.getCities();
//        long numberOfCity = makeRepository.countCities();
//        if (numberOfCity > 0) {
//            makeRepository.deactiveCities();
//            cityList.stream().forEach(item -> {
//                City city = makeRepository.getCityByDescription(item);
//                if (city != null) {
//                    city.setActive(true);
//                }
//                else {
//                    city = new City();
//                    city.setDescription(item);
//                }
//                makeRepository.save(city);
//            });
//        }
//        else {
//            cityList.stream().forEach(item -> {
//                MakeEntity city = new MakeEntity();
//                city.setDescription(item);
//                city.setActive(true);
//                makeRepository.save(city);
//            });
//        }
    }

    @Override
    public List<ModelEntity> getModelList(int makeId) {
        return modelRepository.getAllByMakeId(makeId);
    }


    @Override
    public ModelEntity getModelByModelName(String modelName) {
        return modelRepository.getByModelIgnoreCase(modelName);
    }
}
