package com.demandlane.booklending.member.service;

import com.demandlane.booklending.common.exception.DataInvalidException;
import com.demandlane.booklending.common.exception.ResourceNotFoundException;
import com.demandlane.booklending.member.dto.MemberRequestDto;
import com.demandlane.booklending.member.dto.MemberResponseDto;
import com.demandlane.booklending.member.model.Member;
import com.demandlane.booklending.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService Unit Tests")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member sampleMember;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        sampleMember = new Member();
        sampleMember.setId(memberId);
        sampleMember.setName("John Doe");
        sampleMember.setEmail("john.doe@example.com");
        sampleMember.setPhoneNumber("081234567890");
        sampleMember.setIsActive(true);
    }

    @Nested
    @DisplayName("isMemberExist()")
    class IsMemberExist {
        @Test
        @DisplayName("should return true when member exists")
        void isMemberExist_true() {
            when(memberRepository.existsById(memberId)).thenReturn(true);
            assertThat(memberService.isMemberExist(memberId)).isTrue();
        }

        @Test
        @DisplayName("should return false when member does not exist")
        void isMemberExist_false() {
            when(memberRepository.existsById(memberId)).thenReturn(false);
            assertThat(memberService.isMemberExist(memberId)).isFalse();
        }
    }

    @Nested
    @DisplayName("getMemberById()")
    class GetMemberById {
        @Test
        @DisplayName("should return Optional<Member> when found")
        void getMemberById_found() {
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(sampleMember));
            Optional<Member> result = memberService.getMemberById(memberId);
            assertThat(result).isPresent().contains(sampleMember);
        }

        @Test
        @DisplayName("should return empty Optional when not found")
        void getMemberById_notFound() {
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
            assertThat(memberService.getMemberById(memberId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("getListOfMembers()")
    class GetListOfMembers {
        @Test
        @DisplayName("should return list of MemberResponseDto")
        void getListOfMembers_success() {
            when(memberRepository.findAll()).thenReturn(List.of(sampleMember));
            List<MemberResponseDto> result = memberService.getListOfMembers();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("John Doe");
            assertThat(result.get(0).getEmail()).isEqualTo("john.doe@example.com");
        }

        @Test
        @DisplayName("should return empty list when no members exist")
        void getListOfMembers_empty() {
            when(memberRepository.findAll()).thenReturn(List.of());
            assertThat(memberService.getListOfMembers()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getDetailMembers()")
    class GetDetailMembers {
        @Test
        @DisplayName("should return MemberResponseDto when found")
        void getDetailMembers_success() {
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(sampleMember));
            MemberResponseDto result = memberService.getDetailMembers(memberId);
            assertThat(result.getId()).isEqualTo(memberId);
            assertThat(result.getName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void getDetailMembers_notFound() {
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> memberService.getDetailMembers(memberId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Member not found");
        }
    }

    @Nested
    @DisplayName("createNewMember()")
    class CreateNewMember {
        private MemberRequestDto validRequest;

        @BeforeEach
        void setUp() {
            validRequest = MemberRequestDto.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .phoneNumber("081234567890")
                    .build();
        }

        @Test
        @DisplayName("should create and return MemberResponseDto on success")
        void createNewMember_success() {
            when(memberRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
            when(memberRepository.save(any(Member.class))).thenReturn(sampleMember);

            MemberResponseDto result = memberService.createNewMember(validRequest);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("John Doe");
            verify(memberRepository).save(any(Member.class));
        }

        @Test
        @DisplayName("should throw DataInvalidException when email already exists")
        void createNewMember_duplicateEmail() {
            when(memberRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> memberService.createNewMember(validRequest))
                    .isInstanceOf(DataInvalidException.class)
                    .hasMessageContaining("already exists");

            verify(memberRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateMember()")
    class UpdateMember {
        private MemberRequestDto updateRequest;

        @BeforeEach
        void setUp() {
            updateRequest = MemberRequestDto.builder()
                    .name("John Updated")
                    .email("john.doe@example.com") // same email
                    .phoneNumber("089999999999")
                    .build();
        }

        @Test
        @DisplayName("should update and return MemberResponseDto on success")
        void updateMember_success() {
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(sampleMember));
            when(memberRepository.save(any(Member.class))).thenReturn(sampleMember);

            MemberResponseDto result = memberService.updateMember(memberId, updateRequest);

            assertThat(result).isNotNull();
            verify(memberRepository).save(sampleMember);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when member not found")
        void updateMember_notFound() {
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> memberService.updateMember(memberId, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Member not found");
        }

        @Test
        @DisplayName("should throw DataInvalidException when new email already taken")
        void updateMember_newEmailTaken() {
            updateRequest.setEmail("other@example.com"); // different email
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(sampleMember));
            when(memberRepository.existsByEmail("other@example.com")).thenReturn(true);

            assertThatThrownBy(() -> memberService.updateMember(memberId, updateRequest))
                    .isInstanceOf(DataInvalidException.class)
                    .hasMessageContaining("already exists");
        }
    }

    @Nested
    @DisplayName("deleteMember()")
    class DeleteMember {
        @Test
        @DisplayName("should soft-delete member (set isActive=false)")
        void deleteMember_success() {
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(sampleMember));

            memberService.deleteMember(memberId);

            assertThat(sampleMember.getIsActive()).isFalse();
            assertThat(sampleMember.getDeletedAt()).isNotNull();
            verify(memberRepository).save(sampleMember);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when member not found")
        void deleteMember_notFound() {
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> memberService.deleteMember(memberId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Member not found");
        }
    }
}
