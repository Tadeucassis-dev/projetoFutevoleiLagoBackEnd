package com.escolinhafutevolei.escolinha_futevolei.repository;

import com.escolinhafutevolei.escolinha_futevolei.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}