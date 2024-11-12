package darak.study.spring_study.service;

import darak.study.spring_study.domain.Comment;
import darak.study.spring_study.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    // 댓글 저장
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // 모든 댓글 조회
    @Transactional(readOnly = true)
    public List<Comment> findAllComments() {
        return commentRepository.findAll();
    }

    // 댓글 ID로 조회
    @Transactional(readOnly = true)
    public Optional<Comment> findCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    // 댓글 수정
    public Comment updateComment(Long commentId, Comment updatedComment) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 댓글이 존재하지 않습니다."));

        existingComment.setContent(updatedComment.getContent());
        existingComment.setUpdateDate(updatedComment.getUpdateDate());

        return commentRepository.save(existingComment);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    // 특정 게시글에 속한 댓글 조회
    @Transactional(readOnly = true)
    public List<Comment> findCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // 특정 부모 댓글에 속한 대댓글(답글) 조회
    @Transactional(readOnly = true)
    public List<Comment> findRepliesByParentId(Long parentId) {
        return commentRepository.findByParentCommentId(parentId);
    }
}
