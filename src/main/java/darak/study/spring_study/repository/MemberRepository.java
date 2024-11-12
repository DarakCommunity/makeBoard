package darak.study.spring_study.repository;

import darak.study.spring_study.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository  {
    Member save(Member member);
    Optional<Member> findById(Long id);
    List<Member> findAll();
    void deleteById(Long id);
    Optional<Member> findByEmail(String email);

}
