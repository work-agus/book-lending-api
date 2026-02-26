package com.demandlane.booklending.book.service;

import com.demandlane.booklending.book.dto.BookResponseDto;
import com.demandlane.booklending.book.model.Book;
import com.demandlane.booklending.book.repository.BookRepository;
import com.demandlane.booklending.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookResponseDto> getListOfBooks() {
        return this.bookRepository.findAll().stream().map(book ->
                BookResponseDto.builder()
                        .id(book.getId())
                        .title(book.getTitle())
                        .author(book.getAuthor())
                        .totalCopies(book.getTotalCopies())
                        .availableCopies(book.getAvailableCopies())
                        .build()
        ).toList();
    }

    public BookResponseDto getDetailBook(UUID id) {
        Book book = this.bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        return BookResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .build();
    }

}
