package com.demandlane.booklending.book.service;

import com.demandlane.booklending.book.dto.BookRequestDto;
import com.demandlane.booklending.book.dto.BookResponseDto;
import com.demandlane.booklending.book.model.Book;
import com.demandlane.booklending.book.repository.BookRepository;
import com.demandlane.booklending.common.exception.DataInvalidException;
import com.demandlane.booklending.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Unit Tests")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book sampleBook;
    private UUID bookId;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        sampleBook = new Book();
        sampleBook.setId(bookId);
        sampleBook.setTitle("Clean Code");
        sampleBook.setAuthor("Robert C. Martin");
        sampleBook.setIsbn("978-0132350884");
        sampleBook.setTotalCopies(5);
        sampleBook.setAvailableCopies(3);
        sampleBook.setIsActive(true);
    }

    @Nested
    @DisplayName("getBookById()")
    class GetBookById {
        @Test
        @DisplayName("should return Optional<Book> when found")
        void getBookById_found() {
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(sampleBook));
            Optional<Book> result = bookService.getBookById(bookId);
            assertThat(result).isPresent().contains(sampleBook);
        }

        @Test
        @DisplayName("should return empty Optional when not found")
        void getBookById_notFound() {
            when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
            Optional<Book> result = bookService.getBookById(bookId);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("isBookAvailable()")
    class IsBookAvailable {
        @Test
        @DisplayName("should return true when book is active and has available copies")
        void isBookAvailable_true() {
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(sampleBook));
            assertThat(bookService.isBookAvailable(bookId)).isTrue();
        }

        @Test
        @DisplayName("should return false when available copies is 0")
        void isBookAvailable_noCopies() {
            sampleBook.setAvailableCopies(0);
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(sampleBook));
            assertThat(bookService.isBookAvailable(bookId)).isFalse();
        }

        @Test
        @DisplayName("should return false when book is inactive")
        void isBookAvailable_inactive() {
            sampleBook.setIsActive(false);
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(sampleBook));
            assertThat(bookService.isBookAvailable(bookId)).isFalse();
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when book not found")
        void isBookAvailable_bookNotFound() {
            when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> bookService.isBookAvailable(bookId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Book not found");
        }
    }

    @Nested
    @DisplayName("isBookExist()")
    class IsBookExist {
        @Test
        @DisplayName("should return true when book exists")
        void isBookExist_true() {
            when(bookRepository.existsById(bookId)).thenReturn(true);
            assertThat(bookService.isBookExist(bookId)).isTrue();
        }

        @Test
        @DisplayName("should return false when book does not exist")
        void isBookExist_false() {
            when(bookRepository.existsById(bookId)).thenReturn(false);
            assertThat(bookService.isBookExist(bookId)).isFalse();
        }
    }

    @Nested
    @DisplayName("getListOfBooks()")
    class GetListOfBooks {
        @Test
        @DisplayName("should return list of BookResponseDto")
        void getListOfBooks_success() {
            when(bookRepository.findAll()).thenReturn(List.of(sampleBook));
            List<BookResponseDto> result = bookService.getListOfBooks();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Clean Code");
            assertThat(result.get(0).getAuthor()).isEqualTo("Robert C. Martin");
        }

        @Test
        @DisplayName("should return empty list when no books exist")
        void getListOfBooks_empty() {
            when(bookRepository.findAll()).thenReturn(List.of());
            assertThat(bookService.getListOfBooks()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getDetailBook()")
    class GetDetailBook {
        @Test
        @DisplayName("should return BookResponseDto when book found")
        void getDetailBook_success() {
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(sampleBook));
            BookResponseDto result = bookService.getDetailBook(bookId);
            assertThat(result.getId()).isEqualTo(bookId);
            assertThat(result.getTitle()).isEqualTo("Clean Code");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when book not found")
        void getDetailBook_notFound() {
            when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> bookService.getDetailBook(bookId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Book not found");
        }
    }

    @Nested
    @DisplayName("createNewBook()")
    class CreateNewBook {
        private BookRequestDto validRequest;

        @BeforeEach
        void setUp() {
            validRequest = BookRequestDto.builder()
                    .title("Clean Code")
                    .author("Robert C. Martin")
                    .isbn("978-0132350884")
                    .totalCopies(5)
                    .availableCopies(3)
                    .build();
        }

        @Test
        @DisplayName("should create and return BookResponseDto on success")
        void createNewBook_success() {
            when(bookRepository.existsByIsbn(validRequest.getIsbn())).thenReturn(false);
            when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

            BookResponseDto result = bookService.createNewBook(validRequest);

            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Clean Code");
            verify(bookRepository).save(any(Book.class));
        }

        @Test
        @DisplayName("should throw DataInvalidException when ISBN already exists")
        void createNewBook_duplicateIsbn() {
            when(bookRepository.existsByIsbn(validRequest.getIsbn())).thenReturn(true);

            assertThatThrownBy(() -> bookService.createNewBook(validRequest))
                    .isInstanceOf(DataInvalidException.class)
                    .hasMessageContaining("already exists");

            verify(bookRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw DataInvalidException when availableCopies > totalCopies")
        void createNewBook_invalidCopies() {
            validRequest.setAvailableCopies(10);
            validRequest.setTotalCopies(5);
            when(bookRepository.existsByIsbn(anyString())).thenReturn(false);

            assertThatThrownBy(() -> bookService.createNewBook(validRequest))
                    .isInstanceOf(DataInvalidException.class)
                    .hasMessageContaining("Available copies cannot be greater than total copies");

            verify(bookRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateBook()")
    class UpdateBook {
        private BookRequestDto updateRequest;

        @BeforeEach
        void setUp() {
            updateRequest = BookRequestDto.builder()
                    .title("Clean Code 2nd Ed")
                    .author("Robert C. Martin")
                    .isbn("978-0132350884") // same ISBN
                    .totalCopies(10)
                    .availableCopies(7)
                    .build();
        }

        @Test
        @DisplayName("should update and return BookResponseDto on success")
        void updateBook_success() {
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(sampleBook));
            when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

            BookResponseDto result = bookService.updateBook(bookId, updateRequest);

            assertThat(result).isNotNull();
            verify(bookRepository).save(sampleBook);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when book not found")
        void updateBook_notFound() {
            when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.updateBook(bookId, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Book not found");
        }

        @Test
        @DisplayName("should throw DataInvalidException when new ISBN already taken by another book")
        void updateBook_newIsbnTaken() {
            updateRequest.setIsbn("978-9999999999"); // different ISBN
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(sampleBook));
            when(bookRepository.existsByIsbn("978-9999999999")).thenReturn(true);

            assertThatThrownBy(() -> bookService.updateBook(bookId, updateRequest))
                    .isInstanceOf(DataInvalidException.class)
                    .hasMessageContaining("already exists");
        }

        @Test
        @DisplayName("should throw DataInvalidException when availableCopies > totalCopies")
        void updateBook_invalidCopies() {
            updateRequest.setAvailableCopies(20);
            updateRequest.setTotalCopies(5);
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(sampleBook));

            assertThatThrownBy(() -> bookService.updateBook(bookId, updateRequest))
                    .isInstanceOf(DataInvalidException.class)
                    .hasMessageContaining("Available copies cannot be greater than total copies");
        }
    }

    @Nested
    @DisplayName("deleteBook()")
    class DeleteBook {
        @Test
        @DisplayName("should soft-delete book (set isActive=false)")
        void deleteBook_success() {
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(sampleBook));

            bookService.deleteBook(bookId);

            assertThat(sampleBook.getIsActive()).isFalse();
            assertThat(sampleBook.getDeletedAt()).isNotNull();
            verify(bookRepository).save(sampleBook);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when book not found")
        void deleteBook_notFound() {
            when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.deleteBook(bookId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Book not found");
        }
    }
}
