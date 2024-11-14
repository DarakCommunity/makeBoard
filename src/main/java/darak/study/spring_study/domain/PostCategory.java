package darak.study.spring_study.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
// Lombok 어노테이션으로 모든 필드의 getter/setter 메서드 자동 생성
// PostCategory는 카테고리명 변경 등이 필요할 수 있어 Setter 허용

@Entity
// JPA가 이 클래스를 데이터베이스 테이블과 매핑하도록 지정

@NoArgsConstructor(access = AccessLevel.PROTECTED)
// JPA는 기본 생성자가 필수
// protected로 설정하여 외부에서 직접 생성하는 것을 방지

public class PostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 카테고리의 고유 식별자
    // IDENTITY 전략으로 데이터베이스의 AUTO_INCREMENT 사용

    @Column(nullable = false, unique = true)
    private String name;
    // 카테고리 이름
    // nullable = false: 카테고리 이름은 필수값
    // unique = true: 카테고리 이름은 중복 불가

    @OneToMany(mappedBy = "postCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();
    // 해당 카테고리에 속한 게시글 목록
    // mappedBy: Post 엔티티의 postCategory 필드가 관계의 주인
    // cascade = CascadeType.ALL: 카테고리 삭제 시 관련 게시글도 모두 삭제
    // orphanRemoval = true: 카테고리에서 제거된 게시글은 자동 삭제
    // new ArrayList<>(): NPE 방지를 위한 즉시 초기화
}
