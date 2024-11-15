package darak.study.spring_study.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> childComments = new ArrayList<>();


    @Column(nullable = false, length = 500)
    private String content;

    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    
    // 연관관계 편의 메서드
    public void setPost(Post post) {
        // 기존 관계 제거
        if (this.post != null) {
            this.post.getComments().remove(this);
        }
        this.post = post;
        // 새로운 관계 설정
        if (post != null) {
            post.getComments().add(this);
        }
    }

    // 부모-자식 댓글 관계 설정
    public void addChildComment(Comment child) {
        this.childComments.add(child);
        child.setParentComment(this);
    }

    public void setParentComment(Comment parent) {
        this.parentComment = parent;
    }

    // 댓글 내용 수정
    public void updateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 필수입니다.");
        }
        if (content.length() > 1000) {
            throw new IllegalArgumentException("내용은 1000자를 초과할 수 없습니다.");
        }
        this.content = content;
    }

    // 댓글 상태 변경
    public void changeStatus(CommentStatus status) {
        this.status = status;
    }

    // 삭제 여부 확인
    public boolean isDeleted() {
        return CommentStatus.DELETED.equals(this.status);
    }
    
}
