package darak.study.spring_study.service;

import darak.study.spring_study.domain.Grade;
import darak.study.spring_study.domain.Member;
import darak.study.spring_study.domain.MemberStatus;
import darak.study.spring_study.exception.DuplicateEmailException;
import darak.study.spring_study.exception.InvalidInputException;
import darak.study.spring_study.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("password123")
                .username("테스트유저")
                .phoneNum("010-1234-5678")
                .age(25)
                .grade(Grade.MEMBER)
                .status(MemberStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("회원 가입 테스트")
    class JoinTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            given(memberRepository.findByEmail(any())).willReturn(Optional.empty());
            given(memberRepository.save(any(Member.class))).willReturn(testMember);

            // when
            Long savedId = memberService.join(testMember);

            // then
            assertThat(savedId).isEqualTo(1L);
            verify(memberRepository).save(argThat(savedMember -> 
                BCrypt.checkpw("password123", savedMember.getPassword())
            ));
        }

        @Test
        @DisplayName("실패 - 중복 이메일")
        void failDuplicateEmail() {
            // given
            given(memberRepository.findByEmail(any())).willReturn(Optional.of(testMember));

            // when & then
            assertThatThrownBy(() -> memberService.join(testMember))
                    .isInstanceOf(DuplicateEmailException.class);
        }

        @Test
        @DisplayName("실패 - 잘못된 이메일 형식")
        void failInvalidEmail() {
            // given
            Member invalidMember = testMember.toBuilder()
                    .email("invalid-email")
                    .build();

            // when & then
            assertThatThrownBy(() -> memberService.join(invalidMember))
                    .isInstanceOf(InvalidInputException.class);
        }
    }

    @Nested
    @DisplayName("회원 정보 수정 테스트")
    class UpdateTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Member updatedInfo = testMember.toBuilder()
                    .username("수정된이름")
                    .phoneNum("010-9999-9999")
                    .age(30)
                    .build();
            
            given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));
            given(memberRepository.save(any(Member.class))).willReturn(updatedInfo);

            // when
            Member result = memberService.update(updatedInfo);

            // then
            assertThat(result.getUsername()).isEqualTo("수정된이름");
            assertThat(result.getAge()).isEqualTo(30);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 회원")
        void failMemberNotFound() {
            // given
            given(memberRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.update(testMember))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("해당 ID의 회원이 존재 하지 않습니다");
        }
    }

    @Nested
    @DisplayName("회원 상태 변경 테스트")
    class StatusTest {
        @Test
        @DisplayName("상태 변경 성공")
        void changeStatus() {
            // when
            testMember.changeStatus(MemberStatus.SUSPENDED);
            
            // then
            assertThat(testMember.getStatus()).isEqualTo(MemberStatus.SUSPENDED);
        }

        @Test
        @DisplayName("실패 - null 상태값")
        void failNullStatus() {
            // when & then
            assertThatThrownBy(() -> testMember.changeStatus(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상태값은 필수입니다.");
        }
    }
}