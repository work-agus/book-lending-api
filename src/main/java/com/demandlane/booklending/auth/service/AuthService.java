package com.demandlane.booklending.auth.service;

import com.demandlane.booklending.auth.dto.JwtResponse;
import com.demandlane.booklending.auth.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    @Value("${app.jwt.expiration}")
    private String jwtExpiration;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public JwtResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenUtils.generateJwtToken(authentication);

            String jwtExpirationInset = String.valueOf(Integer.parseInt(jwtExpiration) / 1000);
            return new JwtResponse(
                    jwt,
                    jwtExpirationInset
            );
        } catch (BadCredentialsException e) {
            System.out.println("1 +++++++++++++++++++++++++++++++++++++");
            throw new BadCredentialsException("Error: Invalid username or password");
        } catch (Exception e) {
            System.out.println("2 +++++++++++++++++++++++++++++++++++++");
            throw new BadCredentialsException("Error: Invalid username or password");
        }
    }
}
