package darak.study.spring_study.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Column(nullable = false)
    private CommentStatus status = CommentStatus.ACTIVE;

    
    // 연관관계 편의 메서드
    public void setPost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("게시글은 필수입니다.");
        }
        this.post = post;
    }

    // 부모-자식 댓글 관계 설정
    public void addChildComment(Comment child) {
        if (child == null) {
            throw new IllegalArgumentException("자식 댓글은 null일 수 없습니다.");
        }
        if (this.isDeleted()) {
            throw new IllegalStateException("삭제된 댓글에는 답글을 달 수 없습니다.");
        }
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
        if (status == null) {
            throw new IllegalArgumentException("상태값은 필수입니다.");
        }
        this.status = status;
    }

    // 삭제 여부 확인
    public boolean isDeleted() {
        return CommentStatus.DELETED.equals(this.status);
    }
    
    // 댓글 수정 가능 여부 확인
    public boolean isEditable() {
        return !isDeleted() && status == CommentStatus.ACTIVE;
    }

    // 계층 구조 확인
    public boolean isReply() {
        return parentComment != null;
}
}
