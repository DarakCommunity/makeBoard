package darak.study.spring_study.domain;

import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    private String phoneNum;
    private int age;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @PrePersist
    protected void onCreate() {
        this.password = BCrypt.hashpw(this.password, BCrypt.gensalt());
    }

}
