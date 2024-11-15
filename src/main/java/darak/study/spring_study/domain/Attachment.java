package darak.study.spring_study.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Attachment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 필드명 일관성 유지

    private String contentType;
    private String fileUrl;
    private long fileSize; // int 대신 long 타입으로 변경
    private String fileType;
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false) // 외래 키 설정
    private Post post;

    // 비즈니스 메서드
    public Attachment updateFileInfo(String fileName, String contentType, long fileSize) {
        return this.toBuilder()
                .fileName(fileName)
                .contentType(contentType)
                .fileSize(fileSize)
                .build();
    }

}