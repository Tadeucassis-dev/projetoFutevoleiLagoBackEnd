package com.escolinhafutevolei.escolinha_futevolei.repository;

import com.escolinhafutevolei.escolinha_futevolei.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}