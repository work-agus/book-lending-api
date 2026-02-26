package com.demandlane.booklending.member.service;

import com.demandlane.booklending.book.dto.BookRequestDto;
import com.demandlane.booklending.book.dto.BookResponseDto;
import com.demandlane.booklending.book.model.Book;
import com.demandlane.booklending.book.repository.BookRepository;
import com.demandlane.booklending.common.exception.DataInvalidException;
import com.demandlane.booklending.common.exception.ResourceNotFoundException;
import com.demandlane.booklending.common.util.Utils;
import com.demandlane.booklending.member.dto.MemberRequestDto;
import com.demandlane.booklending.member.dto.MemberResponseDto;
import com.demandlane.booklending.member.model.Member;
import com.demandlane.booklending.member.repository.MemberRepository;
import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository bookRepository) {
        this.memberRepository = bookRepository;
    }

    public List<MemberResponseDto> getListOfMembers() {
        return this.memberRepository.findAll().stream().map(member -> MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .build()).toList();
    }

    public MemberResponseDto getDetailMembers(UUID id) {
        Member member = this.memberRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }

    public MemberResponseDto createNewMember(MemberRequestDto request) {
        if (this.memberRepository.existsByEmail(request.getEmail())) {
            throw new DataInvalidException("Member with Email " + request.getEmail() + " already exists");
        }

        UUID uuid7 = UuidCreator.getTimeOrderedEpoch();

        Member member = new Member();
        member.setId(uuid7);
        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setCreatedBy(Utils.getSystemUUID());

        Member result = this.memberRepository.save(member);
        return MemberResponseDto.builder()
                .id(result.getId())
                .name(result.getName())
                .email(result.getEmail())
                .phoneNumber(result.getPhoneNumber())
                .build();
    }

    public MemberResponseDto updateMember(UUID id, MemberRequestDto request) {
        Member member = this.memberRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (!member.getEmail().equals(request.getEmail()) && this.memberRepository.existsByEmail(request.getEmail())) {
            throw new DataInvalidException("Member with Email " + request.getEmail() + " already exists");
        }

        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setUpdatedBy(Utils.getSystemUUID());

        Member result = this.memberRepository.save(member);
        return MemberResponseDto.builder()
                .id(result.getId())
                .name(result.getName())
                .email(result.getEmail())
                .phoneNumber(result.getPhoneNumber())
                .build();
    }

    public void deleteMember(UUID id) {
        Member member = this.memberRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        member.setIsActive(false);
        member.setDeletedAt(OffsetDateTime.now());
        member.setDeletedBy(Utils.getSystemUUID());
        this.memberRepository.save(member);
    }

}
