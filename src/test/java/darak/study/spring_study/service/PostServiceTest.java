package darak.study.spring_study.service;

import darak.study.spring_study.domain.Post;
import darak.study.spring_study.domain.Member;
import darak.study.spring_study.repository.PostRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ConcurrentModificationException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private Post testPost;

    @BeforeEach
    void setUp() {
        testPost = Post.builder()
                .name("테스트 게시글")
                .content("테스트 내용입니다.")
                .member(Member.builder().id(1L).build())
                .build();
    }

    @Test
    @DisplayName("게시글 생성 성공 테스트")
    void createPostSuccess() {
        // given
        Post post = Post.builder()
                .id(1L)
                .name("테스트 게시글")
                .content("테스트 내용입니다.")
                .member(Member.builder()
                        .id(1L)
                        .email("test@test.com")
                        .build())
                .build();
        
        given(postRepository.save(any(Post.class))).willReturn(post);

        // when
        Long postId = postService.createPost(post);

        // then
        assertThat(postId).isEqualTo(1L);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("제목이 없는 게시글 생성 시 예외 발생")
    void createPostFailNoTitle() {
        // given
        testPost = Post.builder()
                .content("테스트 내용입니다.")
                .member(Member.builder().id(1L).build())
                .build();

        // when & then
        assertThatThrownBy(() -> postService.createPost(testPost))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("게시글 조회 성공 테스트")
    void findPostByIdSuccess() {
        // given
        given(postRepository.findById(any())).willReturn(Optional.of(testPost));

        // when
        Optional<Post> foundPost = postService.findPostById(1L);

        // then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getName()).isEqualTo("테스트 게시글");
    }

    @Test
    @DisplayName("게시글 내용이 5000자를 초과할 때 예외 발생")
    void createPostFailContentTooLong() {
        // given
        String longContent = "a".repeat(5001);
        Post invalidPost = Post.builder()
                .name("테스트")
                .content(longContent)
                .member(Member.builder().id(1L).build())
                .build();

        // when & then
        assertThatThrownBy(() -> postService.createPost(invalidPost))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시글 내용은 5000자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("동시성 처리 - 조회수 증가 실패 시 재시도")
    void incrementViewCountWithRetry() {
        // given
        doThrow(OptimisticLockException.class)
            .doNothing()
            .when(postRepository).incrementViewCount(1L);

        // when
        postService.incrementViewCount(1L);

        // then
        verify(postRepository, times(2)).incrementViewCount(1L);
    }

    @Test
    @DisplayName("동시성 처리 - 좋아요 수 증가 실패 시 재시도 횟수 초과")
    void incrementLikeCountFailAfterRetries() {
        // given
        doThrow(OptimisticLockException.class)
            .when(postRepository).incrementLikeCount(1L);

        // when & then
        assertThatThrownBy(() -> postService.incrementLikeCount(1L))
            .isInstanceOf(ConcurrentModificationException.class)
            .hasMessage("다시 시도해주세요.");
    }
}