package com.demandlane.booklending.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {
    private String title;
    private String author;
    private String isbn;
    private Integer totalCopies;
}