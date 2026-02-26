package com.demandlane.booklending.auth.controller;

import com.demandlane.booklending.auth.dto.JwtResponse;
import com.demandlane.booklending.auth.dto.LoginRequest;
import com.demandlane.booklending.auth.service.AuthService;
import com.demandlane.booklending.auth.service.JwtTokenUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils, AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateClient(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
