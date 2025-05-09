package com.escolinhafutevolei.escolinha_futevolei.service;

import com.escolinhafutevolei.escolinha_futevolei.model.Student;
import com.escolinhafutevolei.escolinha_futevolei.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Student saveStudent(Student student) {
        student.setApproved(false);
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student approveStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setApproved(true);
        return studentRepository.save(student);
    }
}