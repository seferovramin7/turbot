package com.turboparser.turbo.service;

import com.turboparser.turbo.entity.MakeEntity;
import com.turboparser.turbo.entity.ModelEntity;

import java.io.IOException;
import java.util.List;

public interface ModelService {

    void updateModels() throws IOException;

    List<ModelEntity> getModelList(int makeId);

    ModelEntity getModelByModelName(String modelName);

}
