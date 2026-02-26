package com.demandlane.booklending.book.controller;

import com.demandlane.booklending.book.dto.BookRequestDto;
import com.demandlane.booklending.book.dto.BookResponseDto;
import com.demandlane.booklending.book.service.BookService;
import com.demandlane.booklending.common.dto.ResponseDto;
import com.demandlane.booklending.common.util.Utils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN', 'MEMBER')")
    public ResponseEntity<ResponseDto<List<BookResponseDto>>> list() {
        return ResponseEntity.ok(Utils.getResponse(this.bookService.getListOfBooks()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN', 'MEMBER')")
    public ResponseEntity<ResponseDto<BookResponseDto>> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(Utils.getResponse(this.bookService.getDetailBook(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ResponseDto<BookResponseDto>> create(@Valid @RequestBody BookRequestDto request) {
        return ResponseEntity.ok(Utils.getResponse(this.bookService.createNewBook(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ResponseDto<BookResponseDto>> update(@PathVariable UUID id,
            @RequestBody BookRequestDto request) {
        return ResponseEntity.ok(Utils.getResponse(this.bookService.updateBook(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable UUID id) {
        this.bookService.deleteBook(id);
        return ResponseEntity.ok(Utils.getResponse(null));
    }

}
