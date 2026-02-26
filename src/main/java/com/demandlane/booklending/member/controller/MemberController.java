package com.demandlane.booklending.member.controller;

import com.demandlane.booklending.common.dto.ResponseDto;
import com.demandlane.booklending.common.util.Utils;
import com.demandlane.booklending.member.dto.MemberRequestDto;
import com.demandlane.booklending.member.dto.MemberResponseDto;
import com.demandlane.booklending.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/members")
@PreAuthorize("hasRole('ADMIN')")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<MemberResponseDto>>> list() {
        return ResponseEntity.ok(Utils.getResponse(this.memberService.getListOfMembers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<MemberResponseDto>> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(Utils.getResponse(this.memberService.getDetailMembers(id)));
    }

    @PostMapping
    public ResponseEntity<ResponseDto<MemberResponseDto>> create(@Valid @RequestBody MemberRequestDto request) {
        return ResponseEntity.ok(Utils.getResponse(this.memberService.createNewMember(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<MemberResponseDto>> update(@PathVariable UUID id,
            @RequestBody MemberRequestDto request) {
        return ResponseEntity.ok(Utils.getResponse(this.memberService.updateMember(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable UUID id) {
        this.memberService.deleteMember(id);
        return ResponseEntity.ok(Utils.getResponse(null));
    }

}
