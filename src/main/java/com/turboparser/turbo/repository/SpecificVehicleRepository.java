package com.turboparser.turbo.repository;


import com.turboparser.turbo.entity.SpecificVehicleSearchParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecificVehicleRepository extends JpaRepository<SpecificVehicleSearchParameter, String> {
    SpecificVehicleSearchParameter findByLotId(String lotId);
    SpecificVehicleSearchParameter findByIdOrderByLotIdAsc(String lotId);
}