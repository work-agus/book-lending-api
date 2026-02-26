package com.demandlane.booklending.book.model;

import com.demandlane.booklending.common.model.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import java.util.UUID;


@Data
@Where(clause = "deleted_at IS NULL")
@Entity
@Table(name = "books")
@EqualsAndHashCode(callSuper = true)
public class Book extends Auditable {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "author", nullable = false, length = 100)
    private String author;

    @Column(name = "isbn", nullable = false, unique = true, length = 50)
    private String isbn;

    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies = 0;

    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies = 0;
}
