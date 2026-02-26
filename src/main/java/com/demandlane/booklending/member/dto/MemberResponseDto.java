package com.demandlane.booklending.member.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MemberResponseDto {
    public UUID id;
    public String name;
    public String email;
    public String phoneNumber;
}