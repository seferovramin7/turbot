package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.MakeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurboMakeRepository extends JpaRepository<MakeEntity, Long> {
    @Override
    List<MakeEntity> findAll();

    MakeEntity getByMake(String make);
}
