package darak.study.spring_study.service;

import darak.study.spring_study.domain.Attachment;
import darak.study.spring_study.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;

    // 첨부파일 저장
    public Attachment addAttachment(Attachment attachment) {
        return attachmentRepository.save(attachment);
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

    // 첨부파일 수정
    public Attachment updateAttachment(Long attachmentId, Attachment updatedAttachment) {
        Attachment existingAttachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 첨부파일이 존재하지 않습니다."));

        existingAttachment.updateFileInfo(
            updatedAttachment.getFileName(),
            updatedAttachment.getFileType(),
            updatedAttachment.getFileSize()
        );
        return attachmentRepository.save(existingAttachment);
    }

    // 첨부파일 삭제
    public void deleteAttachment(Long attachmentId) {
        attachmentRepository.deleteById(attachmentId);
    }
}
