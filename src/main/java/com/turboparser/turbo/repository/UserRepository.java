package com.turboparser.turbo.repository;

import com.turboparser.turbo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User getUserById(Long id);

}
