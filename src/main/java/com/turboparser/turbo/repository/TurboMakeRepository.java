package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.MakeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurboMakeRepository extends JpaRepository<MakeEntity, Long>, JpaSpecificationExecutor<MakeEntity> {
    @Override
    List<MakeEntity> findAll();

     MakeEntity  findByMake(String make);


    MakeEntity getByMake(String make);

    MakeEntity getByMakeAndMakeId(String make, int makeId);
}
