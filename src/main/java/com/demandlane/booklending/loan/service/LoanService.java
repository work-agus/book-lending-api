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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoanService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoanService.class);

    private final LoanRepository loanRepository;
    private final MemberService memberService;
    private final BookService bookService;

    public LoanService(LoanRepository loanRepository, MemberService memberService, BookService bookService) {
        this.loanRepository = loanRepository;
        this.memberService = memberService;
        this.bookService = bookService;
    }

    public boolean isLoanReachMaxLoan(UUID memberId) {
        LOGGER.info("Checking if member with ID {} has reached the maximum number of borrowed books", memberId);
        return this.loanRepository.findByMemberIdAndReturnedAtIsNull(memberId).size() > Constants.MAX_BORROWED_BOOKS;
    }

    public boolean hasOverdueBooks(UUID memberId) {
        LOGGER.info("Checking if member with ID {} has overdue books", memberId);
        return !this.loanRepository.findByMemberIdAndReturnedAtIsNullAndDueDateAfter(memberId, OffsetDateTime.now()).isEmpty();
    }

    public LoanResponseDto borrowBook(LoanRequestDto loanRequest) {
        LOGGER.info("Processing loan request for member ID {} and book ID {}", loanRequest.getMemberId(), loanRequest.getBookId());

        this.validateLoan(loanRequest.getMemberId(), loanRequest.getBookId());

        Optional<Book> book = this.bookService.getBookById(loanRequest.getBookId());
        Optional<Member> member = this.memberService.getMemberById(loanRequest.getMemberId());

        UUID uuid7 = UuidCreator.getTimeOrderedEpoch();

        LOGGER.info("Creating loan record with ID {} for member ID {} and book ID {}", uuid7, loanRequest.getMemberId(), loanRequest.getBookId());

        Loan loan = new Loan();
        loan.setId(uuid7);
        loan.setBook(book.get());
        loan.setMember(member.get());
        loan.setBorrowedAt(OffsetDateTime.now());
        loan.setDueDate(OffsetDateTime.now().plusDays(Constants.LOAN_PERIOD_DAYS));
        loan.setCreatedBy(Utils.getSystemUUID());

        Loan savedLoan = this.loanRepository.save(loan);

        LOGGER.info("Loan created successfully with ID {} for member ID {} and book ID {}", savedLoan.getId(), loanRequest.getMemberId(), loanRequest.getBookId());

        return LoanResponseDto.builder()
                .id(savedLoan.getId())
                .bookId(savedLoan.getBook().getId())
                .memberId(savedLoan.getMember().getId())
                .borrowedAt(savedLoan.getBorrowedAt().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
                .dueDate(savedLoan.getDueDate().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
                .build();
    }

    public void validateLoan(UUID memberId, UUID bookId) {
        LOGGER.info("Validating loan request for member ID {} and book ID {}", memberId, bookId);

        if (!memberService.isMemberExist(memberId)) {
            LOGGER.error("Validation failed: Member with ID {} not found", memberId);
            throw new ResourceNotFoundException("Member with ID " + memberId + " not found");
        }

        if (!bookService.isBookExist(bookId)) {
            LOGGER.error("Validation failed: Book with ID {} not found", bookId);
            throw new ResourceNotFoundException("Book with ID " + bookId + " not found");
        } else {
            if (!bookService.isBookAvailable(bookId)) {
                LOGGER.error("Validation failed: Book with ID {} is not available", bookId);
                throw new ResourceNotFoundException("Book with ID " + bookId + " is not available");
            }
        }

        if (isLoanReachMaxLoan(memberId)) {
            LOGGER.error("Validation failed: Member with ID {} has reached the maximum number of borrowed books", memberId);
            throw new ResourceNotFoundException("Member with ID " + memberId + " has reached the maximum number of borrowed books");
        } else {
            if (hasOverdueBooks(memberId)) {
                LOGGER.error("Validation failed: Member with ID {} has overdue books", memberId);
                throw new ResourceNotFoundException("Member with ID " + memberId + " has overdue books");
            }
        }
    }
}
