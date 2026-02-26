package com.demandlane.booklending.book.repository;

import com.demandlane.booklending.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    boolean existsByIsbn(String status);
    boolean existsById(UUID id);
}
