package com.demandlane.booklending.auth.service;

import com.demandlane.booklending.auth.dto.JwtResponse;
import com.demandlane.booklending.auth.dto.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService .class);

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
            LOGGER.info("Attempting to authenticate user: {}", request.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenUtils.generateJwtToken(authentication);

            String jwtExpirationInset = String.valueOf(Integer.parseInt(jwtExpiration) / 1000);

            LOGGER.info("User {} authenticated successfully. JWT generated with expiration: {} seconds", request.getUsername(), jwtExpirationInset);

            return new JwtResponse(
                    jwt,
                    jwtExpirationInset
            );
        } catch (BadCredentialsException e) {
            LOGGER.error("Authentication failed for user: {}", request.getUsername(), e);
            throw new BadCredentialsException("Error: Invalid username or password");
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred during authentication for user: {}", request.getUsername(), e);
            throw new BadCredentialsException("Error: Invalid username or password");
        }
    }
}
