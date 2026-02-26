package com.demandlane.booklending.loan.service;

import com.demandlane.booklending.book.model.Book;
import com.demandlane.booklending.book.service.BookService;
import com.demandlane.booklending.common.exception.ResourceNotFoundException;
import com.demandlane.booklending.common.util.Constants;
import com.demandlane.booklending.loan.dto.LoanBorrowRequestDto;
import com.demandlane.booklending.loan.dto.LoanResponseDto;
import com.demandlane.booklending.loan.model.Loan;
import com.demandlane.booklending.loan.repository.LoanRepository;
import com.demandlane.booklending.member.model.Member;
import com.demandlane.booklending.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanService Unit Tests")
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private LoanService loanService;

    private UUID memberId;
    private UUID bookId;
    private Book sampleBook;
    private Member sampleMember;
    private Loan sampleLoan;
    private LoanBorrowRequestDto loanRequest;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        bookId = UUID.randomUUID();

        sampleBook = new Book();
        sampleBook.setId(bookId);
        sampleBook.setTitle("Clean Code");
        sampleBook.setAuthor("Robert C. Martin");
        sampleBook.setAvailableCopies(3);
        sampleBook.setIsActive(true);

        sampleMember = new Member();
        sampleMember.setId(memberId);
        sampleMember.setName("John Doe");
        sampleMember.setEmail("john@example.com");

        sampleLoan = new Loan();
        sampleLoan.setId(UUID.randomUUID());
        sampleLoan.setBook(sampleBook);
        sampleLoan.setMember(sampleMember);
        sampleLoan.setBorrowedAt(OffsetDateTime.now());
        sampleLoan.setDueDate(OffsetDateTime.now().plusDays(Constants.LOAN_PERIOD_DAYS));

        loanRequest = new LoanBorrowRequestDto();
        loanRequest.setMemberId(memberId);
        loanRequest.setBookId(bookId);
    }

    @Nested
    @DisplayName("isLoanReachMaxLoan()")
    class IsLoanReachMaxLoan {
        @Test
        @DisplayName("should return false when active loans <= MAX_BORROWED_BOOKS")
        void isLoanReachMaxLoan_false() {
            when(loanRepository.findByMemberIdAndReturnedAtIsNull(memberId))
                    .thenReturn(List.of(sampleLoan));
            assertThat(loanService.isLoanReachMaxLoan(memberId)).isFalse();
        }

        @Test
        @DisplayName("should return true when active loans exceed MAX_BORROWED_BOOKS")
        void isLoanReachMaxLoan_true() {
            List<Loan> maxLoans = Collections.nCopies(Constants.MAX_BORROWED_BOOKS + 1, sampleLoan);
            when(loanRepository.findByMemberIdAndReturnedAtIsNull(memberId)).thenReturn(maxLoans);
            assertThat(loanService.isLoanReachMaxLoan(memberId)).isTrue();
        }
    }

    @Nested
    @DisplayName("hasOverdueBooks()")
    class HasOverdueBooks {
        @Test
        @DisplayName("should return false when no overdue books")
        void hasOverdueBooks_false() {
            when(loanRepository.findByMemberIdAndReturnedAtIsNullAndDueDateBefore(
                    eq(memberId), any(OffsetDateTime.class)))
                    .thenReturn(List.of());
            assertThat(loanService.hasOverdueBooks(memberId)).isFalse();
        }

        @Test
        @DisplayName("should return true when member has overdue books")
        void hasOverdueBooks_true() {
            when(loanRepository.findByMemberIdAndReturnedAtIsNullAndDueDateBefore(
                    eq(memberId), any(OffsetDateTime.class)))
                    .thenReturn(List.of(sampleLoan));
            assertThat(loanService.hasOverdueBooks(memberId)).isTrue();
        }
    }

    @Nested
    @DisplayName("borrowBook()")
    class BorrowBook {
        private void mockValidLoan() {
            when(memberService.isMemberExist(memberId)).thenReturn(true);
            when(bookService.isBookExist(bookId)).thenReturn(true);
            when(bookService.isBookAvailable(bookId)).thenReturn(true);
            when(loanRepository.findByMemberIdAndReturnedAtIsNull(memberId))
                    .thenReturn(List.of()); // no active loans
            when(loanRepository.findByMemberIdAndReturnedAtIsNullAndDueDateBefore(
                    eq(memberId), any(OffsetDateTime.class)))
                    .thenReturn(List.of()); // no overdue
            when(bookService.getBookById(bookId)).thenReturn(Optional.of(sampleBook));
            when(memberService.getMemberById(memberId)).thenReturn(Optional.of(sampleMember));
            when(loanRepository.save(any(Loan.class))).thenReturn(sampleLoan);
        }

        @Test
        @DisplayName("should create and return LoanResponseDto on success")
        void borrowBook_success() {
            mockValidLoan();

            LoanResponseDto result = loanService.borrowBook(loanRequest);

            assertThat(result).isNotNull();
            assertThat(result.getBookId()).isEqualTo(bookId);
            assertThat(result.getMemberId()).isEqualTo(memberId);
            assertThat(result.getBorrowedAt()).isNotNull();
            assertThat(result.getDueDate()).isNotNull();
            verify(loanRepository).save(any(Loan.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when member not found")
        void borrowBook_memberNotFound() {
            when(memberService.isMemberExist(memberId)).thenReturn(false);

            assertThatThrownBy(() -> loanService.borrowBook(loanRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when book not found")
        void borrowBook_bookNotFound() {
            when(memberService.isMemberExist(memberId)).thenReturn(true);
            when(bookService.isBookExist(bookId)).thenReturn(false);

            assertThatThrownBy(() -> loanService.borrowBook(loanRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when book not available")
        void borrowBook_bookNotAvailable() {
            when(memberService.isMemberExist(memberId)).thenReturn(true);
            when(bookService.isBookExist(bookId)).thenReturn(true);
            when(bookService.isBookAvailable(bookId)).thenReturn(false);

            assertThatThrownBy(() -> loanService.borrowBook(loanRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not available");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when member reached max loans")
        void borrowBook_maxLoanReached() {
            when(memberService.isMemberExist(memberId)).thenReturn(true);
            when(bookService.isBookExist(bookId)).thenReturn(true);
            when(bookService.isBookAvailable(bookId)).thenReturn(true);

            List<Loan> maxLoans = Collections.nCopies(Constants.MAX_BORROWED_BOOKS + 1, sampleLoan);
            when(loanRepository.findByMemberIdAndReturnedAtIsNull(memberId)).thenReturn(maxLoans);

            assertThatThrownBy(() -> loanService.borrowBook(loanRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("maximum number of borrowed books");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when member has overdue books")
        void borrowBook_hasOverdueBooks() {
            when(memberService.isMemberExist(memberId)).thenReturn(true);
            when(bookService.isBookExist(bookId)).thenReturn(true);
            when(bookService.isBookAvailable(bookId)).thenReturn(true);
            when(loanRepository.findByMemberIdAndReturnedAtIsNull(memberId))
                    .thenReturn(List.of()); // under the limit
            when(loanRepository.findByMemberIdAndReturnedAtIsNullAndDueDateBefore(
                    eq(memberId), any(OffsetDateTime.class)))
                    .thenReturn(List.of(sampleLoan)); // has overdue

            assertThatThrownBy(() -> loanService.borrowBook(loanRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("overdue books");
        }
    }

    @Nested
    @DisplayName("validateLoan()")
    class ValidateLoan {
        @Test
        @DisplayName("should pass without exception for a valid loan")
        void validateLoan_valid() {
            when(memberService.isMemberExist(memberId)).thenReturn(true);
            when(bookService.isBookExist(bookId)).thenReturn(true);
            when(bookService.isBookAvailable(bookId)).thenReturn(true);
            when(loanRepository.findByMemberIdAndReturnedAtIsNull(memberId)).thenReturn(List.of());
            when(loanRepository.findByMemberIdAndReturnedAtIsNullAndDueDateBefore(
                    eq(memberId), any(OffsetDateTime.class))).thenReturn(List.of());

            loanService.validateLoan(memberId, bookId);
        }
    }

    @Nested
    @DisplayName("returnBook()")
    class ReturnBook {
        private com.demandlane.booklending.loan.dto.LoanReturnRequestDto returnRequest;

        @BeforeEach
        void setUp() {
            returnRequest = new com.demandlane.booklending.loan.dto.LoanReturnRequestDto();
            returnRequest.setLoanId(sampleLoan.getId());
        }

        @Test
        @DisplayName("should return book successfully")
        void returnBook_success() {
            int originalCopies = sampleBook.getAvailableCopies();
            when(loanRepository.findById(sampleLoan.getId())).thenReturn(Optional.of(sampleLoan));
            when(loanRepository.save(any(Loan.class))).thenReturn(sampleLoan);

            LoanResponseDto result = loanService.returnBook(returnRequest);

            assertThat(result).isNotNull();
            assertThat(result.getReturnedAt()).isNotNull();
            assertThat(sampleBook.getAvailableCopies()).isEqualTo(originalCopies + 1);
            verify(loanRepository).save(sampleLoan);
            verify(bookService).saveBook(sampleBook);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when loan not found")
        void returnBook_loanNotFound() {
            when(loanRepository.findById(sampleLoan.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> loanService.returnBook(returnRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when loan already returned")
        void returnBook_alreadyReturned() {
            sampleLoan.setReturnedAt(OffsetDateTime.now());
            when(loanRepository.findById(sampleLoan.getId())).thenReturn(Optional.of(sampleLoan));

            assertThatThrownBy(() -> loanService.returnBook(returnRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("already been returned");
        }
    }
}
