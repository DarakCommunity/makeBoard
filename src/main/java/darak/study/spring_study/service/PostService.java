package darak.study.spring_study.service;

import darak.study.spring_study.domain.Post;
import darak.study.spring_study.dto.PostUpdateRequest;
import darak.study.spring_study.dto.PostPageResponse;
import darak.study.spring_study.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        if (post.getName() == null || post.getName().isEmpty()) {
            throw new IllegalArgumentException("게시글 제목은 필수입니다.");
        }
        if (post.getContent() == null || post.getContent().isEmpty()) {
            throw new IllegalArgumentException("게시글 내용은 필수입니다.");
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

    // 게시글 수정
    @Transactional
    public Post updatePost(Post updatingPost) {
        Post existingPost = postRepository.findById(updatingPost.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글이 존재하지 않습니다."));

        Post updatedPost = Post.builder()
                .id(existingPost.getId())
                .name(updatingPost.getName() != null ? updatingPost.getName() : existingPost.getName())
                .content(updatingPost.getContent() != null ? updatingPost.getContent() : existingPost.getContent())
                .postCategory(updatingPost.getPostCategory() != null ? updatingPost.getPostCategory() : existingPost.getPostCategory())
                .member(existingPost.getMember()) // 연관된 필드 유지
                .comments(existingPost.getComments()) // 연관된 필드 유지
                .likeCount(existingPost.getLikeCount())
                .build();
        return postRepository.save(updatedPost);
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
        return postRepository.findByIdWithComments(postId);
    }

    @Transactional(readOnly = true)
    public PostPageResponse findPostsWithPaging(int page, int size) {
        List<Post> posts = postRepository.findAllWithPaging((page - 1) * size, size);
        long totalCount = postRepository.count();
        
        return new PostPageResponse(
            posts,
            page,
            size,
            totalCount,
            (totalCount + size - 1) / size
        );
    }

    // 동시성 처리를 위한 메서드
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :id")
    public void incrementLikeCount(Long postId) {
        postRepository.incrementLikeCount(postId);
    }

    // 게시글 수정 (단일 메서드로 통합)
    public void updatePost(Long postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("해당 id의 게시글을 찾을 수 없습니다"));
        post.update(request.getName(), request.getContent());
    }

}
