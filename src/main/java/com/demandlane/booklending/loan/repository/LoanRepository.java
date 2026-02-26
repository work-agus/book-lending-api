package com.demandlane.booklending.loan.repository;

import com.demandlane.booklending.loan.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByMemberIdAndReturnedAtIsNull(UUID memberId);
    List<Loan> findByMemberIdAndReturnedAtIsNullAndDueDateBefore(UUID memberId, OffsetDateTime now);
}