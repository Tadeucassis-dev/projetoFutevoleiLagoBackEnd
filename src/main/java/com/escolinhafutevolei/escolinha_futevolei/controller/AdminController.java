package com.escolinhafutevolei.escolinha_futevolei.controller;


import com.escolinhafutevolei.escolinha_futevolei.model.Student;
import com.escolinhafutevolei.escolinha_futevolei.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PostMapping("/approve-student/{id}")
    public ResponseEntity<Student> approveStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.approveStudent(id));
    }
}