package com.escolinhafutevolei.escolinha_futevolei.controller;

import com.escolinhafutevolei.escolinha_futevolei.config.JwtTokenProvider;
import com.escolinhafutevolei.escolinha_futevolei.model.User;
import com.escolinhafutevolei.escolinha_futevolei.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String name,
                                      @RequestParam String email,
                                      @RequestParam String password) {
        try {
            if (userService.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body("Email já está em uso");
            }

            User user = userService.registerUser(name, email, password);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao registrar: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("Recebido: email=" + request.getEmail() + ", password=***");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            String token = jwtTokenProvider.generateToken(authentication);
            User user = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            System.out.println("Login bem-sucedido para: " + user.getEmail());
            return ResponseEntity.ok(new AuthResponse(token, user));
        } catch (BadCredentialsException e) {
            System.out.println("Credenciais inválidas para email: " + request.getEmail());
            return ResponseEntity.status(401).body(new ErrorResponse("Credenciais inválidas"));
        } catch (Exception e) {
            System.out.println("Erro no login: " + e.getMessage());
            return ResponseEntity.status(401).body(new ErrorResponse("Erro ao processar o login: " + e.getMessage()));
        }
    }

    static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    static class AuthResponse {
        private String token;
        private User user;

        public AuthResponse(String token, User user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }
    }

    static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}