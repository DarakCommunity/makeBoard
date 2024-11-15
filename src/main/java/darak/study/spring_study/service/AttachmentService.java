package darak.study.spring_study.service;

import darak.study.spring_study.domain.Attachment;
import darak.study.spring_study.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentService {
    private static final long MAX_FILE_SIZE = 10_000_000L; // 10MB로 파일 크기 제한
    private static final int MAX_ATTACHMENTS_PER_POST = 5; // 하나의 게시글당 최대 5개의 첨부파일 제한
    private static final Set<String> ALLOWED_FILE_TYPES = Set.of( // 허용된 파일 형식 정의
        "image/jpeg", "image/png", "image/gif", 
        "application/pdf", "application/msword"
    );

    private final AttachmentRepository attachmentRepository;

    // 첨부파일 저장
    public Attachment addAttachment(Attachment attachment) {
        // 게시글 검증을 먼저 수행
        if (attachment.getPost() == null) {
            throw new IllegalArgumentException("첨부파일은 반드시 게시글에 속해야 합니다.");
        }

        // 나머지 유효성 검증
        validateAttachment(attachment);
        
        // 첨부파일 개수 제한 검증
        validateAttachmentCount(attachment.getPost().getId());
        
        return attachmentRepository.save(attachment);
    }

    // 첨부파일 수정
    public Attachment updateAttachment(Long attachmentId, Attachment updatedAttachment) {
        Attachment existingAttachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 첨부파일이 존재하지 않습니다."));
        
        // 공통 유효성 검증 사용
        validateAttachment(updatedAttachment);

        existingAttachment.updateFileInfo(
            updatedAttachment.getFileName(),
            updatedAttachment.getFileType(),
            updatedAttachment.getFileSize()
        );
        return attachmentRepository.save(existingAttachment);
    }

    // 공통 유효성 검증 메서드
    private void validateAttachment(Attachment attachment) {
        // 파일명 검증
        if (attachment.getFileName() == null || attachment.getFileName().isBlank()) {
            throw new IllegalArgumentException("파일 이름은 필수입니다.");
        }

        // 파일 크기 검증
        if (attachment.getFileSize() == null || attachment.getFileSize() <= 0) {
            throw new IllegalArgumentException("유효하지 않은 파일 크기입니다.");
        }
        if (attachment.getFileSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
        }

        // 파일 타입 검증
        if (attachment.getFileType() == null || !ALLOWED_FILE_TYPES.contains(attachment.getFileType())) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        }
    }

    // 게시글별 첨부파일 개수 검증 (신규 추가시에만 필요)
    private void validateAttachmentCount(Long postId) {
        if (attachmentRepository.findByPostId(postId).size() >= MAX_ATTACHMENTS_PER_POST) {
            throw new IllegalStateException(
                "하나의 게시글당 최대 " + MAX_ATTACHMENTS_PER_POST + "개의 첨부파일만 허용됩니다.");
        }
    }

    // 모든 첨부파일 조회
    @Transactional(readOnly = true)
    public List<Attachment> findAllAttachments() {
        return attachmentRepository.findAll();
    }

    // 첨부파일 ID로 조회
    @Transactional(readOnly = true)
    public Optional<Attachment> findAttachmentById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId);
    }

    // 특정 게시글에 속한 첨부파일 조회
    @Transactional(readOnly = true)
    public List<Attachment> findAttachmentsByPostId(Long postId) {
        return attachmentRepository.findByPostId(postId);
    }

    // 첨부파일 삭제
    public void deleteAttachment(Long attachmentId) {
        attachmentRepository.deleteById(attachmentId);
    }

}
