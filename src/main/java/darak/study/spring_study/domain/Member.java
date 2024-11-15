package darak.study.spring_study.domain;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                 // 회원 고유 식별자
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Grade grade = Grade.MEMBER;                            // 회원 등급 (ADMIN, MEMBER)
    
    @Column(length = 20)
    private String phoneNum;                        // 전화번호
    
    @Column(nullable = false)
    private int age;                                // 나이
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;                           // 이메일 (중복 불가)
    
    @Column(nullable = false, length = 50)
    private String password;                        // 비밀번호 (필수 값)
    
    @Column(nullable = false, length = 50)
    private String username;                        // 사용자 이름
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;                    // 회원 상태
    
    // 회원 정보 업데이트 메서드 개선
    public void update(String username, String phoneNum, int age, Grade grade) {
        validateUpdate(username, age, grade);
        
        this.username = username;
        this.phoneNum = phoneNum;
        this.age = age;
        this.grade = grade;
    }

    // 유효성 검증 메서드 추가
    private void validateUpdate(String username, int age, Grade grade) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }
        if (age < 0) {
            throw new IllegalArgumentException("나이는 0보다 작을 수 없습니다.");
        }
        if (grade == null) {
            throw new IllegalArgumentException("회원 등급은 필수입니다.");
        }
    }

    // 추가 제안: 상태 변경 메서드
    public void changeStatus(MemberStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("상태값은 필수입니다.");
        }
        this.status = status;
    }

    // 추가 제안: 비밀번호 변경 메서드
    public void updatePassword(String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        this.password = newPassword;
    }
}


