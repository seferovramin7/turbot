package com.turboparser.turbo.repository;


import com.turboparser.turbo.entity.SpecificVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecificVehicleRepository extends JpaRepository<SpecificVehicle, Long> {
    SpecificVehicle findByLotId(String lotId);
}