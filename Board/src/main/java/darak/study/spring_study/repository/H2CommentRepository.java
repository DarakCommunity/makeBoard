package darak.study.spring_study.repository;

import darak.study.spring_study.domain.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class H2CommentRepository implements CommentRepository{

    @PersistenceContext
    private EntityManager em;


//    댓글 저장
    @Override
    public Comment save(Comment comment) {
        if(comment.getId() == null){
            em.persist(comment);
            return comment;
        }
        else {
            return em.merge(comment);
        }
    }

//    댓글 id로 조회
    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

//    모든 댓글 조회
    @Override
    public List<Comment> findAll() {
        return em.createQuery("select c from Comment c", Comment.class)
                .getResultList();
    }

//   댓글 id로 댓글 삭제
    @Override
    public void deleteById(Long id) {
        Comment comment = em.find(Comment.class, id);
        if(comment != null){
            em.remove(comment);
        }
        else{
            throw new IllegalArgumentException("해당 id를 가진 댓글은 존재하지 않습니다");
        }

    }

//    특정 게시글에 속한 댓글 조회
    @Override
    public List<Comment> findByPostId(Long postId) {
        return em.createQuery("select c from Comment c where c.post.id = :postId", Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

//    특정 문자열을 포함한 댓글 조회
    @Override
    public List<Comment> findContentContaining(String keyword) {
        return em.createQuery("select c from Comment c where lower(c.content) Like lower(:keyword)", Comment.class)
                .setParameter("keyword","%"+keyword+"%")
                .getResultList();
    }

//    부모댓글에 달린 모든 댓글 조회
    @Override
    public List<Comment> findByParentCommentId(Long parentId) {
        return em.createQuery("select c from Comment c where c.parentComment.id = :parentId", Comment.class)
                .setParameter("parentId", parentId)
                .getResultList();
    }
}
