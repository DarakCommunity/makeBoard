package darak.study.spring_study.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Attachment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 필드명 일관성 유지

    private String fileUrl;
    private long fileSize; // int 대신 long 타입으로 변경
    private String fileType;
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false) // 외래 키 설정
    private Post post;

    // 비즈니스 메서드
    // updateFileInfo 메서드에서 불필요한 builder 패턴 사용을 간소화
    public void updateFileInfo(String fileName, String fileType, long fileSize) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

}