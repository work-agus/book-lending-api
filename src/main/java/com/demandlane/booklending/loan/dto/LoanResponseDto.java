package com.demandlane.booklending.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDto {
    public UUID id;
    public UUID bookId;
    public UUID memberId;
    public String borrowedAt;
    public String dueDate;
    public String returnedAt;
}