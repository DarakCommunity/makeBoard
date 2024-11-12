package darak.study.spring_study.service;

import darak.study.spring_study.domain.Member;
import darak.study.spring_study.repository.H2MemberRepository;
import darak.study.spring_study.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    //   회원 가입
    public Long join (Member member){
        validateDuplicateMember(member); // 중복 회원 확인
        validateMemberFields(member);
        memberRepository.save(member);
        return member.getId();

    }

//     회원 정보 필드 유효성 확인
    private void validateMemberFields(Member member) {
        if (member.getAge() <= 0) {
            throw new IllegalArgumentException("나이는 양수여야 합니다.");
        }
        if (member.getEmail() == null || !member.getEmail().contains("@")) {
            throw new IllegalArgumentException("유효한 이메일 주소를 입력해주세요.");
        }
        // 필요한 경우 추가 필드 검증
    }

//     중복 검증
    private void validateDuplicateMember(Member member) {
        memberRepository.findByEmail(member.getEmail())
                .ifPresent(member1 -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다");
                });
    }

    //    전체 회원 조회
    @Transactional(readOnly = true)
    public List<Member> findMembers(){
            return memberRepository.findAll();
    }

//    아이디로 조회
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long memberId){
        return memberRepository.findById(memberId);
    }

//    아이디로 회원 삭제
    public void delete(Long memberId){
        memberRepository.deleteById(memberId);
    }


//   이메일로 조회
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email){
        return memberRepository.findByEmail(email);
    }

//    회원 업데이트
    public Member update(Member updatingMember){
        Member existingMember = memberRepository.findById(updatingMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원이 존재 하지 않습니다"));

        Member updatedMember = Member.builder()
                .id(existingMember.getId())
                .grade(updatingMember.getGrade() != null ? updatingMember.getGrade() : existingMember.getGrade() )
                .phoneNum(updatingMember.getPhoneNum() != null ? updatingMember.getPhoneNum() : existingMember.getPhoneNum() )
                .age(updatingMember.getAge() <=0 ? existingMember.getAge() : updatingMember.getAge() )
                .email(existingMember.getEmail() != null ? existingMember.getEmail() : updatingMember.getEmail() )
                .build();

//        내부적으로 동일 맴버이면 덮어씌움
        return memberRepository.save(updatedMember);
    }
}
