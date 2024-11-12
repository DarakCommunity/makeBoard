package darak.study.spring_study.repository;

import darak.study.spring_study.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class H2MemberRepository implements MemberRepository {

@PersistenceContext
private EntityManager em;


// 회원 저장
    @Override
    public Member save(Member member) {
        if(member.getId()==null){
//            신규 회원이면 새로 저장
            em.persist(member);
            return member;
        }
        else {
            return em.merge(member);
        }
    }

//아이디로 회원 조회
    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(em.find(Member.class, id));
    }

//   회원 전부 조회
    @Override
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

//    아이디로 회원 삭제
    @Override
    public void deleteById(Long id) {
        Member member = em.find(Member.class, id);
        if (member != null) {
            em.remove(member);
        }
        else {
            throw new IllegalArgumentException("해당 id의 회원이 존재 하지 않습니다");
        }
    }

//    이메일로 회원 조회
    @Override
    public Optional<Member> findByEmail(String email) {
        List<Member> results = em.createQuery("SELECT m FROM Member m WHERE m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();
        return results.stream().findFirst();
    }
}


