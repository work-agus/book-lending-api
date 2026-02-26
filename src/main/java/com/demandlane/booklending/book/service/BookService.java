package com.demandlane.booklending.book.service;

import com.demandlane.booklending.book.dto.BookRequestDto;
import com.demandlane.booklending.book.dto.BookResponseDto;
import com.demandlane.booklending.book.model.Book;
import com.demandlane.booklending.book.repository.BookRepository;
import com.demandlane.booklending.common.exception.DataInvalidException;
import com.demandlane.booklending.common.exception.ResourceNotFoundException;
import com.github.f4b6a3.uuid.UuidCreator;
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
        return this.bookRepository.findAll().stream().map(book -> BookResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .build()).toList();
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

    public BookResponseDto createNewBook(BookRequestDto request) {
        if (this.bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DataInvalidException("Book with ISBN " + request.getIsbn() + " already exists");
        }

        UUID uuid7 = UuidCreator.getTimeOrderedEpoch();

        Book book = new Book();
        book.setId(uuid7);
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies() > 0);

        Book result = this.bookRepository.save(book);
        return BookResponseDto.builder()
                .id(result.getId())
                .title(result.getTitle())
                .author(result.getAuthor())
                .totalCopies(result.getTotalCopies())
                .availableCopies(result.getAvailableCopies())
                .build();
    }
}
