package darak.study.spring_study.repository;



import darak.study.spring_study.domain.Post;



import java.util.List;

import java.util.Optional;



import org.springframework.data.jpa.repository.Query;



public interface PostRepository {



//    게시글 저장

    Post save(Post post);





//   게시글 id로 조회

    Optional<Post> findById(Long id);



//    모든 게시글 조회

    List<Post> findAll();



//    게시글 삭제

    void deleteById(Long id);



    // 작성자 ID로 게시글 조회

    List<Post> findByAuthorId(Long authorId);



    // 제목에 특정 문자열이 포함된 게시글 조회

    List<Post> findByNameContaining(String keyword);



//    제목,내용에 특정 문자열이 포홤된 게시글 조회

    List<Post> findByNameOrContentContaining(String keyword);



    // 동시성 문제가 발생할 수 있는 코드

    public void incrementLikeCount(Long postId);



    // N+1 문제 해결을 위한 메서드 추가

    Optional<Post> findByIdWithMemberAndComments(Long id);

    

    // 페이징을 위한 메서드 추가

    List<Post> findAllWithPaging(int offset, int limit);

    

    // 게시글 수 조회

    long count();





}


