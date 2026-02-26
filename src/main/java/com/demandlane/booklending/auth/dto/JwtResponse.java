package com.demandlane.booklending.auth.dto;

import lombok.Data;

@Data
public class JwtResponse {
    private String accessToken;
    private String expiredIn;

    public JwtResponse(String accessToken, String expiredIn) {
        this.accessToken = accessToken;
        this.expiredIn = expiredIn;
    }
}
