package darak.study.spring_study.service;

import darak.study.spring_study.domain.Member;
import darak.study.spring_study.domain.MemberStatus;
import darak.study.spring_study.repository.MemberRepository;
import darak.study.spring_study.exception.InvalidInputException;
import darak.study.spring_study.exception.DuplicateEmailException;
import darak.study.spring_study.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    //   회원 가입
    public Long join (Member member){
        validateDuplicateMember(member); // 중복 회원 확인
        validateMemberFields(member);
        
         // 비밀번호 암호화는 서비스 계층에서 처리
        String encodedPassword = BCrypt.hashpw(member.getPassword(), BCrypt.gensalt());
        Member newMember = Member.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(encodedPassword)
                .username(member.getUsername())
                .phoneNum(member.getPhoneNum())
                .age(member.getAge())
                .grade(member.getGrade())
                .status(MemberStatus.ACTIVE)
                .build();
                
        memberRepository.save(newMember);
        return newMember.getId();
    }

    //     회원 정보 필드 유효성 확인
    private void validateMemberFields(Member member) {
        if (member.getAge() <= 0) {
            throw new InvalidInputException("나이는 양수여야 합니다.");
        }
        if (member.getEmail() == null || !member.getEmail().contains("@")) {
            throw new InvalidInputException("유효한 이메일 주소를 입력해주세요.");
        }
        // 필요한 경우 추가 필드 검증
    }

    //     중복 검증
    private void validateDuplicateMember(Member member) {
        memberRepository.findByEmail(member.getEmail())
                .ifPresent(m -> {
                    throw new DuplicateEmailException(member.getEmail());
                });
    }

    //    전체 회원 조회
    @Transactional(readOnly = true)
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    //    아이디로 조회
    @Transactional(readOnly = true)
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    //    아이디로 회원 삭제
    public void delete(Long memberId){
        memberRepository.deleteById(memberId);
    }

    //   이메일로 조회
    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(email));
    }

    //    회원 업데이트
    public Member update(Member updatingMember) {
        Member existingMember = memberRepository.findById(updatingMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원이 존재 하지 않습니다"));

        existingMember.update(
            updatingMember.getUsername(),
            updatingMember.getPhoneNum(),
            updatingMember.getAge(),
            updatingMember.getGrade()
        );

        return memberRepository.save(existingMember);
    }
}


