package com.escolinhafutevolei.escolinha_futevolei.repository;

import com.escolinhafutevolei.escolinha_futevolei.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}