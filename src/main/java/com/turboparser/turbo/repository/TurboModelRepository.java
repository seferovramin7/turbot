package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.ModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurboModelRepository extends JpaRepository<ModelEntity, Long> {
}
