package com.escolinhafutevolei.escolinha_futevolei.repository;

import com.escolinhafutevolei.escolinha_futevolei.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
