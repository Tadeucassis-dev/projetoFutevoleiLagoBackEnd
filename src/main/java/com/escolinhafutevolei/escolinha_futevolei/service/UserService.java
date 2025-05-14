package com.escolinhafutevolei.escolinha_futevolei.service;

import com.escolinhafutevolei.escolinha_futevolei.model.Role;
import com.escolinhafutevolei.escolinha_futevolei.model.Student;
import com.escolinhafutevolei.escolinha_futevolei.model.User;
import com.escolinhafutevolei.escolinha_futevolei.repository.RoleRepository;
import com.escolinhafutevolei.escolinha_futevolei.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initRoles() {
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }
    }

    public User registerUser(String name, String email, String password) {
        System.out.println("Registrando usuário: " + email);
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER não encontrada"));
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        User savedUser = userRepository.save(user);
        System.out.println("Usuário salvo: " + savedUser.getEmail());
        return savedUser;
    }

    public void registerStudent(String name, String age, String schoolUnit,
                                MultipartFile identityFile, MultipartFile attendanceFile) {
        System.out.println("Registrando aluno: " + name + ", idade: " + age + ", unidade escolar: " + schoolUnit);
        Student student = new Student();
        student.setName(name);
        student.setAge(Integer.parseInt(age));
        student.setSchoolUnit(schoolUnit);
        // Salvar os arquivos de identidade e presença
        System.out.println("Salvando arquivos: " + identityFile.getOriginalFilename() + ", " + attendanceFile.getOriginalFilename());
        try {
            Files.createDirectories(Paths.get("uploads/identity"));
            Files.createDirectories(Paths.get("uploads/attendance"));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar diretórios: " + e.getMessage());
        }

        try {
            Files.write(Paths.get("uploads/identity/" + identityFile.getOriginalFilename()),
                       identityFile.getBytes());
            Files.write(Paths.get("uploads/attendance/" + attendanceFile.getOriginalFilename()),
                       attendanceFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivos: " + e.getMessage());
        }

    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .map(name -> name.replace("ROLE_", ""))
                        .toArray(String[]::new))
                .build();
    }
}