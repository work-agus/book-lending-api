package com.demandlane.booklending.book.service;

import com.demandlane.booklending.book.dto.BookRequestDto;
import com.demandlane.booklending.book.dto.BookResponseDto;
import com.demandlane.booklending.book.model.Book;
import com.demandlane.booklending.book.repository.BookRepository;
import com.demandlane.booklending.common.exception.DataInvalidException;
import com.demandlane.booklending.common.exception.ResourceNotFoundException;
import com.demandlane.booklending.common.util.Utils;
import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Optional<Book> getBookById(UUID id) {
        return this.bookRepository.findById(id);
    }

    public boolean isBookAvailable(UUID id) {
        Book book = this.bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        return book.getAvailableCopies() > 0 && book.getIsActive();
    }

    public boolean isBookExist(UUID id) {
        return this.bookRepository.existsById(id);
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

        if (request.getAvailableCopies() > request.getTotalCopies()) {
            throw new DataInvalidException("Available copies cannot be greater than total copies");
        }

        UUID uuid7 = UuidCreator.getTimeOrderedEpoch();

        Book book = new Book();
        book.setId(uuid7);
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getAvailableCopies());
        book.setCreatedBy(Utils.getSystemUUID());

        Book result = this.bookRepository.save(book);
        return BookResponseDto.builder()
                .id(result.getId())
                .title(result.getTitle())
                .author(result.getAuthor())
                .totalCopies(result.getTotalCopies())
                .availableCopies(result.getAvailableCopies())
                .build();
    }

    public BookResponseDto updateBook(UUID id, BookRequestDto request) {
        Book book = this.bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (!book.getIsbn().equals(request.getIsbn()) && this.bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DataInvalidException("Book with ISBN " + request.getIsbn() + " already exists");
        }

        if (request.getAvailableCopies() > request.getTotalCopies()) {
            throw new DataInvalidException("Available copies cannot be greater than total copies");
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getAvailableCopies());
        book.setUpdatedBy(Utils.getSystemUUID());

        Book result = this.bookRepository.save(book);
        return BookResponseDto.builder()
                .id(result.getId())
                .title(result.getTitle())
                .author(result.getAuthor())
                .totalCopies(result.getTotalCopies())
                .availableCopies(result.getAvailableCopies())
                .build();
    }

    public void deleteBook(UUID id) {
        Book book = this.bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        book.setIsActive(false);
        book.setDeletedAt(OffsetDateTime.now());
        book.setDeletedBy(Utils.getSystemUUID());
        this.bookRepository.save(book);
    }

}
