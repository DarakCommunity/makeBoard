package darak.study.spring_study.domain;

import jakarta.persistence.*;
import lombok.*;

import javax.naming.Name;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postCategoryID", nullable = false)
    private PostCategory postCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    private String name;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private int likeCount;
    private String content;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }

    public void update(String name, String content) {
        this.name = name;
        this.content = content;
        this.updateDate = LocalDateTime.now();
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setPost(this);
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

}
