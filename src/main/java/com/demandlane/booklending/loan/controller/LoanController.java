package com.demandlane.booklending.loan.controller;

import com.demandlane.booklending.common.dto.ResponseDto;
import com.demandlane.booklending.common.util.Utils;
import com.demandlane.booklending.loan.dto.LoanBorrowRequestDto;
import com.demandlane.booklending.loan.dto.LoanResponseDto;
import com.demandlane.booklending.loan.dto.LoanReturnRequestDto;
import com.demandlane.booklending.loan.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/borrow")
    public ResponseEntity<ResponseDto<LoanResponseDto>> borrowBook(@Valid @RequestBody LoanBorrowRequestDto requestDto) {
        return ResponseEntity.ok(Utils.getResponse(this.loanService.borrowBook(requestDto)));
    }

    @PostMapping("/return")
    public ResponseEntity<ResponseDto<LoanResponseDto>> returnBook(@Valid @RequestBody LoanReturnRequestDto requestDto) {
        return ResponseEntity.ok(Utils.getResponse(this.loanService.returnBook(requestDto)));
    }

}
