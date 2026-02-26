package com.demandlane.booklending.loan.model;

import com.demandlane.booklending.book.model.Book;
import com.demandlane.booklending.common.model.Auditable;
import com.demandlane.booklending.member.model.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Where(clause = "deleted_at IS NULL")
@Entity
@Table(name = "loans")
@EqualsAndHashCode(callSuper = true)
public class Loan extends Auditable {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "borrowed_at", nullable = false)
    private OffsetDateTime borrowedAt;

    @Column(name = "due_date", nullable = false)
    private OffsetDateTime dueDate;

    @Column(name = "returned_at")
    private OffsetDateTime returnedAt;
}
