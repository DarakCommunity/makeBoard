package darak.study.spring_study.domain;

import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                 // 회원 고유 식별자
    
    @Enumerated(EnumType.STRING)
    private Grade grade;                            // 회원 등급 (ADMIN, MEMBER)
    
    private String phoneNum;                        // 전화번호
    private int age;                                // 나이
    
    @Column(unique = true)
    private String email;                           // 이메일 (중복 불가)
    
    @Column(nullable = false)
    private String password;                        // 비밀번호 (필수 값)
    
    private String username;                        // 사용자 이름
    
    @Enumerated(EnumType.STRING)
    private MemberStatus status;                    // 회원 상태
    

}


