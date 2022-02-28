package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.MakeCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MakeCodeRepository extends JpaRepository<MakeCodeEntity, Long> {
}
