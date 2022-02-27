package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.VehicleArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleArchiveRepository extends JpaRepository<VehicleArchive, Long> {
    VehicleArchive getByLotAndPrice(String lot, String Price);
}
