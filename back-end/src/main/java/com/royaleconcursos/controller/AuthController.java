package com.royaleconcursos.controller;

import com.royaleconcursos.dto.ErrorResponse;
import com.royaleconcursos.dto.LoginRequest;
import com.royaleconcursos.dto.RegisterRequest;
import com.royaleconcursos.dto.Response;
import com.royaleconcursos.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequiredArgsConstructor

public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest body) {
        System.out.println("CHEGOU NO CONTROLLER");
        try {
            return ResponseEntity.ok(userService.register(body));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest body) {
        return ResponseEntity.ok(userService.login(body));
    }
}
