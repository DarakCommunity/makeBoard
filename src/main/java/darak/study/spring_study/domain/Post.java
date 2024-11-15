package darak.study.spring_study.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import darak.study.spring_study.exception.UnauthorizedException;

@Entity  
// JPA가 이 클래스를 데이터베이스 테이블과 매핑하도록 지정
// 이 어노테이션이 있으면 JPA가 이 클래스의 인스턴스를 데이터베이스에 자동으로 저장/조회 가능

@Getter  
// Lombok 어노테이션으로 모든 필드의 getter 메서드를 자동 생성
// setter는 의도적으로 제외 (불변성 유지와 안전한 객체 상태 관리를 위해)

@NoArgsConstructor(access = AccessLevel.PROTECTED)  
// JPA는 기본 생성자가 필수
// protected로 설정하여 외부에서 직접 생성하는 것을 방지
// Builder 패턴을 통해서만 객체 생성이 가능하도록 제한

@AllArgsConstructor(access = AccessLevel.PRIVATE)   
// Builder 패턴 구현을 위해 필요
// private으로 설정하여 외부에서 직접 호출 불가능

@Builder(toBuilder = true)
// 객체 생성을 위한 빌더 패턴 구현
// 많은 필드가 있을 때 가독성 있고 안전한 객체 생성 가능
public class Post extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                // 게시글 고유 식별자
    // @Id: 기본 키 지정
    // @GeneratedValue: 기본 키 자동 생성
    // IDENTITY 전략: 데이터베이스의 AUTO_INCREMENT 기능 사용
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postCategoryID", nullable = false)
    private PostCategory postCategory;              // 게시글 카테고리
    // @ManyToOne: 다대일 관계 설정 (여러 게시글이 하나의 카테고리에 속함)
    // fetch = FetchType.LAZY: N+1 문제 방지를 위한 지연 로딩
    // @JoinColumn: 외래 키 컬럼 지정
    // nullable = false: 카테고리는 필수값


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;                          // 작성자
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();  // 댓글 목록
    // @OneToMany: 일대다 관계 설정 (하나의 게시글이 여러 댓글을 가짐)
    // mappedBy: 양방향 관계에서 주인을 지정 (Comment 엔티티의 post 필드가 주인)
    // cascade = CascadeType.ALL: 게시글에 대한 모든 작업이 댓글에도 전파
    // orphanRemoval = true: 게시글에서 제거된 댓글은 자동 삭제

    @Column(nullable = false)
    private String name;                            // 게시글 제목
    
    @Builder.Default
    @Column(columnDefinition = "integer default 0")
    private int likeCount=0;                          // 좋아요 수
    
    
    @Column(nullable = false, length = 5000)
    private String content;                         // 게시글 내용
    
    
    @Builder.Default
    @Column(columnDefinition = "integer default 0")
    private int viewCount=0;                          // 조회수
    
    @Version
    @Builder.Default
    private Long version = 0L;                      // 동시성 제어를 위한 버전
    // 낙관적 락(Optimistic Lock)을 위한 버전 관리
    // 동시성 제어: 여러 사용자가 동시에 같은 게시글을 수정할 때 발생할 수 있는 문제 방지

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PostStatus status = PostStatus.DRAFT;                      // 게시글 상태
    
    // 게시글 수정
    public void update(String name, String content) {
        this.name = name;
        this.content = content;
    }
    
    // 댓글 추가
    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setPost(this);
    }
    // 양방향 관계 설정을 위한 편의 메서드
    // 댓글 추가 시 양쪽 모두 관계를 설정하여 일관성 유지
    
   
    // 게시글 삭제
     public void softDelete() {
        this.status = PostStatus.DELETED;
    }
    
    // 게시글 삭제 여부 확인
    public boolean isDeleted() {
        return PostStatus.DELETED.equals(this.status);
    }

    // 게시글이 공개 상태인지 확인
    public boolean isPublic() {
        return PostStatus.PUBLIC.equals(this.status);
    }
    
    // 게시글 작성자 검증
    public void validateWriter(Member member) {
        if (!this.member.equals(member)) {
            throw new UnauthorizedException("게시글의 작성자가 아닙니다.");
        }
    }
}


