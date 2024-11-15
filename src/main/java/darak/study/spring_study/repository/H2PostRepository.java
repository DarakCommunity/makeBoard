package darak.study.spring_study.repository;

import darak.study.spring_study.domain.Post;
import darak.study.spring_study.domain.PostStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class H2PostRepository implements PostRepository{

    @PersistenceContext
    private EntityManager em;


//    게시글 저장
    @Override
    public Post save(Post post) {
        if(post.getId() == null) {
//            신규 게시글이면 저장
            em.persist(post);
            return post;
        }
        else {
//            기존에 글이 존재하면 덮어씌우기
            return em.merge(post);
        }
    }

//    id로 게시글 조회
    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(em.find(Post.class, id));
    }

//    모든 게시글 조회
    @Override
    public List<Post> findAll() {
        return em.createQuery("select p from Post p", Post.class)
                .getResultList();
    }

//    id로 게시글 삭제
    @Override
    public void deleteById(Long id) {
        Post post = em.find(Post.class, id);
        if(post != null) {
            em.remove(post);
        }
        else {
            throw new IllegalArgumentException("해당 id의 post는 존재하지 않습니다");
        }

    }

//    작성자 id로 게시글 조회
    @Override
    public List<Post> findByAuthorId(Long authorId) {
        return em.createQuery("select p from Post p where p.member.id = :authorId", Post.class)
                .setParameter("authorId", authorId)
                .getResultList();
    }

//    제목에 키워드가 들어간 게시글 조회
    @Override
    public List<Post> findByNameContaining(String keyword) {
        return em.createQuery("select p from Post p where lower(p.name) Like lower(:keyword)", Post.class)
                .setParameter("keyword", "%"+keyword+"%")
                .getResultList();
    }

//    제목이나 내용에 키워드가 들어간 게시글 조회
    @Override
    public List<Post> findByNameOrContentContaining(String keyword) {
        return em.createQuery("select p from Post p where lower(p.name) Like lower(:keyword) or lower(p.content) Like lower(:keyword)", Post.class)
                .setParameter("keyword", "%"+keyword+"%")
                .getResultList();
    }

    // N+1 문제 해결을 위한 메서드
    @Override
    public Optional<Post> findByIdWithMemberAndComments(Long id) {
        return em.createQuery(
            "SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.member " +  // 작성자 정보
            "LEFT JOIN FETCH p.comments c " +  // 댓글
            "LEFT JOIN FETCH c.member " +  // 댓글 작성자
            "WHERE p.id = :id AND p.status <> :status", Post.class)
            .setParameter("id", id)
            .setParameter("status", PostStatus.DELETED)
            .getResultStream()
            .findFirst();
    }
    
    // 페이징을 위한 메서드
    @Override
    public List<Post> findAllWithPaging(int offset, int limit) {
        return em.createQuery(
            "SELECT p FROM Post p " +
            "WHERE p.status <> :status " +
            "ORDER BY p.createDate DESC", Post.class)
            .setParameter("status", PostStatus.DELETED)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
    }
    
    // 게시글 수 조회
    @Override
    public long count() {
        return em.createQuery("SELECT COUNT(p) FROM Post p", Long.class)
            .getSingleResult();
    }

    @Override
    public void incrementViewCount(Long postId) {
        try {
            int updatedCount = em.createQuery(
                "UPDATE Post p " +
                "SET p.viewCount = p.viewCount + 1, " +
                "p.version = p.version + 1 " +
                "WHERE p.id = :id " +
                "AND p.version = :version " +
                "AND p.status <> :status")
                .setParameter("id", postId)
                .setParameter("version", getVersion(postId))
                .setParameter("status", PostStatus.DELETED)
                .executeUpdate();
                
            if (updatedCount == 0) {
                throw new OptimisticLockException("동시성 문제가 발생했습니다");
            }
        } catch (Exception e) {
            throw new OptimisticLockException("조회수 증가 중 오류가 발생했습니다", e);
        }
    }

    // 동시성 처리 개선
    @Override
    public void incrementLikeCount(Long id) {
        try {
            int updatedCount = em.createQuery(
                "UPDATE Post p " +
                "SET p.likeCount = p.likeCount + 1, " +
                "p.version = p.version + 1 " +
                "WHERE p.id = :id " +
                "AND p.version = :version " +
                "AND p.status <> :status")
                .setParameter("id", id)
                .setParameter("version", getVersion(id))
                .setParameter("status", PostStatus.DELETED)
                .executeUpdate();
                
            if (updatedCount == 0) {
                throw new OptimisticLockException("동시성 문제가 발생했습니다");
            }
        } catch (Exception e) {
            throw new OptimisticLockException("좋아요 증가 중 오류가 발생했습니다", e);
        }
    }

    private Long getVersion(Long id) {
        Long version = em.createQuery("SELECT p.version FROM Post p WHERE p.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return version != null ? version : 0L;
    }
}
