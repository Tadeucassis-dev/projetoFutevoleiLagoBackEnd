package com.escolinhafutevolei.escolinha_futevolei.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;
    private String schoolUnit;
    private String identityFilePath;
    private String attendanceFilePath;
    private boolean approved;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}