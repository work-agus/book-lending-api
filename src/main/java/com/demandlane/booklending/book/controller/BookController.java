package com.demandlane.booklending.book.controller;

import com.demandlane.booklending.book.dto.BookRequestDto;
import com.demandlane.booklending.book.dto.BookResponseDto;
import com.demandlane.booklending.book.service.BookService;
import com.demandlane.booklending.common.dto.ResponseDto;
import com.demandlane.booklending.common.util.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ResponseDto<List<BookResponseDto>>> list() {
        return ResponseEntity.ok(Utils.getResponse(this.bookService.getListOfBooks()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<BookResponseDto>> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(Utils.getResponse(this.bookService.getDetailBook(id)));
    }

    @PostMapping
    public ResponseEntity<ResponseDto<BookResponseDto>> create(@RequestBody BookRequestDto request) {
        return ResponseEntity.ok(Utils.getResponse(this.bookService.createNewBook(request)));
    }
}
