package darak.study.spring_study.repository;

import darak.study.spring_study.domain.Attachment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class H2AttachmentRepository implements AttachmentRepository{

    @PersistenceContext
    private EntityManager em;


//    첨부파일 저장
    @Override
    public Attachment save(Attachment attachment) {
        if(attachment.getId() == null){
            em.persist(attachment);
            return attachment;
        }
        else{
            return em.merge(attachment);
        }


    }

//    id로 첨부파일 조회
    @Override
    public Optional<Attachment> findById(Long id) {
        return Optional.ofNullable(em.find(Attachment.class, id));
    }

//    모든 첨부파일들 조회
    @Override
    public List<Attachment> findAll() {
        return em.createQuery("select a from Attachment a", Attachment.class)
                .getResultList();
    }

//    id로 첨부파일 삭제
    @Override
    public void deleteById(Long id) {
        Attachment attachment = em.find(Attachment.class, id);
        if(attachment != null){
            em.remove(attachment);
        }
        else {
            throw new IllegalArgumentException("해당 id를 가진 첨부파일은 존재하지 않습니다");
        }

    }

//    게시글에 포함된 첨부파일 모두 조회
    @Override
    public List<Attachment> findByPostId(Long postId) {
        return em.createQuery("select a from Attachment a where a.post.id = :postId", Attachment.class)
                .setParameter("postId", postId)
                .getResultList();
    }
}
