package com.escolinhafutevolei.escolinha_futevolei.controller;


import com.escolinhafutevolei.escolinha_futevolei.model.Student;
import com.escolinhafutevolei.escolinha_futevolei.model.User;
import com.escolinhafutevolei.escolinha_futevolei.service.FileStorageService;
import com.escolinhafutevolei.escolinha_futevolei.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/home")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to the Futevolei School Home Page");
    }

    @PostMapping("/register-student")
    public ResponseEntity<Student> registerStudent(
            @RequestParam String name,
            @RequestParam int age,
            @RequestParam String schoolUnit,
            @RequestParam MultipartFile identityFile,
            @RequestParam MultipartFile attendanceFile) {
        String identityPath = fileStorageService.storeFile(identityFile);
        String attendancePath = fileStorageService.storeFile(attendanceFile);

        Student student = new Student();
        student.setName(name);
        student.setAge(age);
        student.setSchoolUnit(schoolUnit);
        student.setIdentityFilePath(identityPath);
        student.setAttendanceFilePath(attendancePath);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = new User();
        user.setUsername(username);
        student.setUser(user);

        return ResponseEntity.ok(studentService.saveStudent(student));
    }
}