package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.CarTypeEntity;
import com.turboparser.turbo.entity.VehicleArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarTypeRepository extends JpaRepository<CarTypeEntity, Long> {
}
