package com.escolinhafutevolei.escolinha_futevolei.service;

import com.escolinhafutevolei.escolinha_futevolei.model.Role;
import com.escolinhafutevolei.escolinha_futevolei.model.Student;
import com.escolinhafutevolei.escolinha_futevolei.model.User;
import com.escolinhafutevolei.escolinha_futevolei.repository.RoleRepository;
import com.escolinhafutevolei.escolinha_futevolei.repository.StudentRepository;
import com.escolinhafutevolei.escolinha_futevolei.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
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
    private StudentRepository studentRepository;

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

    public void registerStudent(String name, String age, String schoolUnit, String fone, String email,
                                MultipartFile identityFile, MultipartFile attendanceFile) {
        try {
            // Validar parâmetros
            if (name == null || age == null || schoolUnit == null || fone == null || email == null ||
                    identityFile == null || attendanceFile == null) {
                throw new IllegalArgumentException("Todos os campos são obrigatórios");
            }

            // Salvar arquivos
            String identityFilePath = "uploads/identity/" + identityFile.getOriginalFilename();
            String attendanceFilePath = "uploads/attendance/" + attendanceFile.getOriginalFilename();
            Files.createDirectories(Paths.get("Uploads/identity"));
            Files.createDirectories(Paths.get("Uploads/attendance"));
            Files.write(Paths.get(identityFilePath), identityFile.getBytes());
            Files.write(Paths.get(attendanceFilePath), attendanceFile.getBytes());

            // Criar e salvar a entidade Student
            Student student = new Student();
            student.setName(name);
            student.setAge(Integer.parseInt(age));
            student.setSchoolUnit(schoolUnit);
            student.setFone(fone);
            student.setEmail(email);
            student.setIdentityFilePath(identityFilePath);
            student.setAttendanceFilePath(attendanceFilePath);
            student.setApproved(false);
            student.setUser(null); // Cadastro público, sem usuário vinculado

            studentRepository.save(student);
            System.out.println("Aluno salvo: " + name);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar aluno: " + e.getMessage());
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
