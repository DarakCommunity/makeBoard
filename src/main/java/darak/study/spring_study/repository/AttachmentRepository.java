package darak.study.spring_study.repository;

import darak.study.spring_study.domain.Attachment;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository {
    // 첨부파일 저장
    Attachment save(Attachment attachment);

    // 첨부파일 ID로 조회
    Optional<Attachment> findById(Long id);

    // 모든 첨부파일 조회
    List<Attachment> findAll();

    // 첨부파일 삭제
    void deleteById(Long id);

    // 특정 게시글에 속한 모든 첨부파일 조회
    List<Attachment> findByPostId(Long postId);
}
