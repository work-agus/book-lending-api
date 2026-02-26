package com.demandlane.booklending.loan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDto {
    @NotNull(message = "Member ID is required")
    private UUID memberId;

    @NotNull(message = "Book ID is required")
    private UUID bookId;
}
