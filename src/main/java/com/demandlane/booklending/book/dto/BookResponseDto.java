package com.demandlane.booklending.book.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BookResponseDto {
    public UUID id;
    public String title;
    public String author;
    public Integer totalCopies;
    public Boolean availableCopies;
}