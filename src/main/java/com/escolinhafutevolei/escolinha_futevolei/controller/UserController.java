package com.escolinhafutevolei.escolinha_futevolei.controller;

import com.escolinhafutevolei.escolinha_futevolei.config.JwtTokenProvider;

import com.escolinhafutevolei.escolinha_futevolei.model.User;
import com.escolinhafutevolei.escolinha_futevolei.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password) {
        System.out.println("Recebido: name=" + name + ", email=" + email + ", password=" + password);
        try {
            if (userService.findByEmail(email).isPresent()) {
                System.out.println("Email já está em uso: " + email);
                return ResponseEntity.badRequest().body(new ErrorResponse() {
                    @Override
                    public HttpStatusCode getStatusCode() {
                        return null;
                    }

                    @Override
                    public ProblemDetail getBody() {
                        return null;
                    }
                });
            }
            User user = userService.registerUser(name, email, password);
            System.out.println("Usuário registrado: " + user.getEmail());
            String token = jwtTokenProvider.generateTokenByEmail(user.getEmail());
            System.out.println("Token gerado: " + token);
            return ResponseEntity.ok(new AuthController.AuthResponse(token, user));
        } catch (Exception e) {
            System.out.println("Erro ao registrar usuário: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ErrorResponse() {
                @Override
                public HttpStatusCode getStatusCode() {
                    return null;
                }

                @Override
                public ProblemDetail getBody() {
                    return null;
                }
            });
        }
    }
}