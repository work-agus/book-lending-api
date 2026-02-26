package com.demandlane.booklending.book.controller;

import com.demandlane.booklending.book.dto.BookResponseDto;
import com.demandlane.booklending.book.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> list() {
        return ResponseEntity.ok(this.bookService.getListOfBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(this.bookService.getDetailBook(id));
    }
}
