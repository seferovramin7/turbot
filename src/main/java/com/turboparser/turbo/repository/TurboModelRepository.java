package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.MakeEntity;
import com.turboparser.turbo.entity.ModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurboModelRepository extends JpaRepository<ModelEntity, Long> {
    @Override
    List<ModelEntity> findAll();

    ModelEntity getByModelIgnoreCase(String model);

    List<ModelEntity> getAllByMakeId(int makeId);

    ModelEntity getByModelAndModelIdAndMakeId(String model,int modelId, int makeId);
}
