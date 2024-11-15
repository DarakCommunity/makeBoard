package darak.study.spring_study.repository;

import darak.study.spring_study.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    // 댓글 저장
    Comment save(Comment comment);

    // 댓글 ID로 조회
    Optional<Comment> findById(Long id);

    // 모든 댓글 조회
    List<Comment> findAll();

    // 댓글 삭제
    void deleteById(Long id);

    // 특정 게시글 ID에 속한 모든 댓글 조회
    List<Comment> findByPostId(Long postId);

    // 특정 문자열을 포함한 댓글 조회
    List<Comment> findContentContaining(String keyword);

    // 특정 부모 댓글에 속한 모든 대댓글 조회
    List<Comment> findByParentCommentId(Long parentId);
}
