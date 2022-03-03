package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
