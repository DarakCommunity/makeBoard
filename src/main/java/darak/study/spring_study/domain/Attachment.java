package darak.study.spring_study.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 필드명 일관성 유지

    private String contentType;
    private String fileUrl;
    private long fileSize; // int 대신 long 타입으로 변경
    private String fileType;
    private String fileName;

    private LocalDateTime createDate; // LocalDateTime 타입으로 변경
    private LocalDateTime updateDate; // LocalDateTime 타입으로 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false) // 외래 키 설정
    private Post post;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}