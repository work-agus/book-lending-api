package com.demandlane.booklending.loan.service;

import com.demandlane.booklending.book.model.Book;
import com.demandlane.booklending.book.service.BookService;
import com.demandlane.booklending.common.exception.ResourceNotFoundException;
import com.demandlane.booklending.common.util.Constants;
import com.demandlane.booklending.common.util.Utils;
import com.demandlane.booklending.loan.dto.LoanRequestDto;
import com.demandlane.booklending.loan.dto.LoanResponseDto;
import com.demandlane.booklending.loan.model.Loan;
import com.demandlane.booklending.loan.repository.LoanRepository;
import com.demandlane.booklending.member.model.Member;
import com.demandlane.booklending.member.service.MemberService;
import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final MemberService memberService;
    private final BookService bookService;

    public LoanService(LoanRepository loanRepository, MemberService memberService, BookService bookService) {
        this.loanRepository = loanRepository;
        this.memberService = memberService;
        this.bookService = bookService;
    }

    public boolean isLoanReachMaxLoan(UUID memberId) {
        return this.loanRepository.findByMemberIdAndReturnedAtIsNull(memberId).size() > Constants.MAX_BORROWED_BOOKS;
    }

    public boolean hasOverdueBooks(UUID memberId) {
        return !this.loanRepository.findByMemberIdAndReturnedAtIsNullAndDueDateAfter(memberId, OffsetDateTime.now()).isEmpty();
    }

    public LoanResponseDto borrowBook(LoanRequestDto loanRequest) {
        this.validateLoan(loanRequest.getMemberId(), loanRequest.getBookId());

        Optional<Book> book = this.bookService.getBookById(loanRequest.getBookId());
        Optional<Member> member = this.memberService.getMemberById(loanRequest.getMemberId());

        UUID uuid7 = UuidCreator.getTimeOrderedEpoch();

        Loan loan = new Loan();
        loan.setId(uuid7);
        loan.setBook(book.get());
        loan.setMember(member.get());
        loan.setBorrowedAt(OffsetDateTime.now());
        loan.setDueDate(OffsetDateTime.now().plusDays(Constants.LOAN_PERIOD_DAYS));
        loan.setCreatedBy(Utils.getSystemUUID());

        Loan savedLoan = this.loanRepository.save(loan);
        return LoanResponseDto.builder()
                .id(savedLoan.getId())
                .bookId(savedLoan.getBook().getId())
                .memberId(savedLoan.getMember().getId())
                .borrowedAt(savedLoan.getBorrowedAt().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
                .dueDate(savedLoan.getDueDate().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
                .build();
    }

    public void validateLoan(UUID memberId, UUID bookId) {
        if (!memberService.isMemberExist(memberId)) {
            throw new ResourceNotFoundException("Member with ID " + memberId + " not found");
        }

        if (!bookService.isBookExist(bookId)) {
            throw new ResourceNotFoundException("Book with ID " + bookId + " not found");
        } else {
            if (!bookService.isBookAvailable(bookId)) {
                throw new ResourceNotFoundException("Book with ID " + bookId + " is not available");
            }
        }

        if (isLoanReachMaxLoan(memberId)) {
            throw new ResourceNotFoundException("Member with ID " + memberId + " has reached the maximum number of borrowed books");
        } else {
            if (hasOverdueBooks(memberId)) {
                throw new ResourceNotFoundException("Member with ID " + memberId + " has overdue books");
            }
        }
    }
}
