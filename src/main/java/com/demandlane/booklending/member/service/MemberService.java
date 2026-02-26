package com.demandlane.booklending.member.service;

import com.demandlane.booklending.common.exception.DataInvalidException;
import com.demandlane.booklending.common.exception.ResourceNotFoundException;
import com.demandlane.booklending.common.util.Utils;
import com.demandlane.booklending.member.dto.MemberRequestDto;
import com.demandlane.booklending.member.dto.MemberResponseDto;
import com.demandlane.booklending.member.model.Member;
import com.demandlane.booklending.member.repository.MemberRepository;
import com.github.f4b6a3.uuid.UuidCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository bookRepository) {
        this.memberRepository = bookRepository;
    }

    public boolean isMemberExist(UUID id) {
        return this.memberRepository.existsById(id);
    }

    public Optional<Member> getMemberById(UUID id) {
        LOGGER.info("Fetching member with ID: {}", id);
        return this.memberRepository.findById(id);
    }

    public List<MemberResponseDto> getListOfMembers() {
        LOGGER.info("Fetching list of all members from the repository");
        return this.memberRepository.findAll().stream().map(member -> MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .build()).toList();
    }

    public MemberResponseDto getDetailMembers(UUID id) {
        LOGGER.info("Fetching details for member with ID: {}", id);
        Member member = this.memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }

    @Transactional
    public MemberResponseDto createNewMember(MemberRequestDto request) {
        LOGGER.info("Creating new member with email: {}", request.getEmail());

        if (this.memberRepository.existsByEmail(request.getEmail())) {
            LOGGER.error("Member with email {} already exists", request.getEmail());
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

        LOGGER.info("Member created successfully with ID: {}", result.getId());
        return MemberResponseDto.builder()
                .id(result.getId())
                .name(result.getName())
                .email(result.getEmail())
                .phoneNumber(result.getPhoneNumber())
                .build();
    }

    @Transactional
    public MemberResponseDto updateMember(UUID id, MemberRequestDto request) {
        LOGGER.info("Updating member with ID: {}", id);

        Member member = this.memberRepository.findById(id).orElseThrow(() -> {
            LOGGER.error("Member with ID {} not found for update", id);
            return new ResourceNotFoundException("Member not found");
        });

        if (!member.getEmail().equals(request.getEmail()) && this.memberRepository.existsByEmail(request.getEmail())) {
            LOGGER.error("Member with email {} already exists", request.getEmail());
            throw new DataInvalidException("Member with Email " + request.getEmail() + " already exists");
        }

        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setUpdatedBy(Utils.getSystemUUID());

        Member result = this.memberRepository.save(member);

        LOGGER.info("Member with ID {} updated successfully", result.getId());

        return MemberResponseDto.builder()
                .id(result.getId())
                .name(result.getName())
                .email(result.getEmail())
                .phoneNumber(result.getPhoneNumber())
                .build();
    }

    @Transactional
    public void deleteMember(UUID id) {
        LOGGER.info("Deleting member with ID: {}", id);

        Member member = this.memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        member.setIsActive(false);
        member.setDeletedAt(OffsetDateTime.now());
        member.setDeletedBy(Utils.getSystemUUID());
        this.memberRepository.save(member);

        LOGGER.info("Member with ID {} deleted successfully", id);
    }

}
