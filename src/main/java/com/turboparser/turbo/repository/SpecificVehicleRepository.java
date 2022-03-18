package com.turboparser.turbo.repository;


import com.turboparser.turbo.entity.SpecificVehicleSearchParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecificVehicleRepository extends JpaRepository<SpecificVehicleSearchParameter, Long> {
    SpecificVehicleSearchParameter findByLotId(Long lotId);
    SpecificVehicleSearchParameter findByChat_ChatIdAndLotId(Long chatId ,Long lotId);

    List<SpecificVehicleSearchParameter> findAllByLotId(Long lotId);
}