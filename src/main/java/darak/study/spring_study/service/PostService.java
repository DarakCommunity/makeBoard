package darak.study.spring_study.service;

import darak.study.spring_study.domain.Post;
import darak.study.spring_study.dto.PostUpdateRequest;
import darak.study.spring_study.dto.PostPageResponse;
import darak.study.spring_study.repository.PostRepository;
import darak.study.spring_study.dto.PostResponse;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 게시글 생성
    public Long createPost(Post post) {
        validatePostFields(post);  // 필드 유효성 검사
        postRepository.save(post);
        return post.getId();
    }

    // 게시글 필드 유효성 검사

    private void validatePostFields(Post post) {
        if (post.getName() == null || post.getName().isBlank()) {
            throw new IllegalArgumentException("게시글 제목은 필수입니다.");
        }
        if (post.getContent() == null || post.getContent().isBlank()) {
            throw new IllegalArgumentException("게시글 내용은 필수입니다.");
        }
        if (post.getContent().length() > 5000) {
            throw new IllegalArgumentException("게시글 내용은 5000자를 초과할 수 없습니다.");
        }
    }

    private void validatePagingParameters(int page, int size) {
        if (page < 1) {
            throw new IllegalArgumentException("페이지 번호는 1 이상이어야 합니다.");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("페이지 크기는 1에서 100 사이여야 합니다.");
        }
    }

    // 모든 게시글 조회
    @Transactional(readOnly = true)
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    // 게시글 ID로 조회
    @Transactional(readOnly = true)
    public Optional<Post> findPostById(Long postId) {
        return postRepository.findById(postId);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    // 특정 작성자의 게시글 조회
    @Transactional(readOnly = true)
    public List<Post> findPostsByAuthorId(Long authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    // 제목이나 내용에 특정 키워드가 포함된 게시글 조회
    @Transactional(readOnly = true)
    public List<Post> findByNameOrContentContaining(String keyword) {
        return postRepository.findByNameOrContentContaining(keyword);
    }

    // 제목에 특정 키워드가 포함된 게시글 조회
    @Transactional(readOnly = true)
    public List<Post> findByTitleContaining(String keyword) {
        return postRepository.findByNameContaining(keyword);
    }

    @Transactional(readOnly = true)
    public Optional<Post> findPostWithComments(Long postId) {
        return postRepository.findByIdWithMemberAndComments(postId);
    }

    @Transactional(readOnly = true)
    public PostPageResponse findPostsWithPaging(int page, int size) {
        validatePagingParameters(page, size);

        List<Post> posts = postRepository.findAllWithPaging((page - 1) * size, size);
        long totalCount = postRepository.count();
        
       return PostPageResponse.builder()
            .posts(posts.stream()
                .map(PostResponse::from)
                .collect(Collectors.toList()))
            .currentPage(page)
            .pageSize(size)
            .totalCount(totalCount)
            .totalPages((totalCount + size - 1) / size)
            .build();
    }

    @Transactional
    public void incrementLikeCount(Long postId) {
        try {
            handleOptimisticLockException(() -> 
                postRepository.incrementLikeCount(postId));
        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("다시 시도해주세요.");
        }
    }


    @Transactional
    public void incrementViewCount(Long postId) {
        try {
            handleOptimisticLockException(() -> 
                postRepository.incrementViewCount(postId));
        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("다시 시도해주세요.");
        }
    }


    // 게시글 수정 (단일 메서드로 통합)
    public void updatePost(Long postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("해당 id의 게시글을 찾을 수 없습니다"));
        post.update(request.getName(), request.getContent());
    }

    // 공통 예외 처리 메서드
    private void handleOptimisticLockException(Runnable operation) {
        int maxRetries = 3;        // 최대 3번까지 재시도
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                operation.run();    // 실제 작업 실행
                return;            // 성공하면 메서드 종료
            } catch (OptimisticLockException e) {
                retryCount++;
                if (retryCount == maxRetries) {  // 3번 실패하면
                    throw e;                      // 예외 발생
                }
                try {
                    Thread.sleep(100);  // 100ms 대기 후 재시도
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("처리가 중단되었습니다.", ie);
                }
            }
        }
    }

}
