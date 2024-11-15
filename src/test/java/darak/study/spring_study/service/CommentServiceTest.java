package darak.study.spring_study.service;

import darak.study.spring_study.domain.Comment;
import darak.study.spring_study.domain.CommentStatus;
import darak.study.spring_study.domain.Post;
import darak.study.spring_study.domain.Member;
import darak.study.spring_study.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment testComment;

    @BeforeEach
    void setUp() {
        testComment = Comment.builder()
                .content("테스트 댓글입니다.")
                .post(Post.builder().id(1L).build())
                .member(Member.builder().id(1L).build())
                .build();
    }

    @Test
    @DisplayName("댓글 저장 성공 테스트")
    void addCommentSuccess() {
        // given
        given(commentRepository.save(any(Comment.class))).willReturn(testComment);

        // when
        Comment savedComment = commentService.addComment(testComment);

        // then
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("테스트 댓글입니다.");
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("모든 댓글 조회 테스트")
    void findAllCommentsSuccess() {
        // given
        List<Comment> comments = Arrays.asList(testComment);
        given(commentRepository.findAll()).willReturn(comments);

        // when
        List<Comment> foundComments = commentService.findAllComments();

        // then
        assertThat(foundComments).hasSize(1);
        assertThat(foundComments.get(0).getContent()).isEqualTo("테스트 댓글입니다.");
    }

    @Test
    @DisplayName("댓글 ID로 조회 테스트")
    void findCommentByIdSuccess() {
        // given
        given(commentRepository.findById(1L)).willReturn(Optional.of(testComment));

        // when
        Optional<Comment> foundComment = commentService.findCommentById(1L);

        // then
        assertThat(foundComment).isPresent();
        assertThat(foundComment.get().getContent()).isEqualTo("테스트 댓글입니다.");
    }

    @Test
    @DisplayName("댓글 수정 성공 테스트")
    void updateCommentSuccess() {
        // given
        Comment updatedComment = Comment.builder()
                .content("수정된 댓글입니다.")
                .build();
        given(commentRepository.findById(1L)).willReturn(Optional.of(testComment));
        given(commentRepository.save(any(Comment.class))).willReturn(updatedComment);

        // when
        Comment result = commentService.updateComment(1L, updatedComment);

        // then
        assertThat(result.getContent()).isEqualTo("수정된 댓글입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 댓글 수정 시 예외 발생")
    void updateCommentNotFound() {
        // given
        given(commentRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(1L, testComment))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 ID의 댓글이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("게시글 ID로 댓글 조회 테스트")
    void findCommentsByPostIdSuccess() {
        // given
        List<Comment> comments = Arrays.asList(testComment);
        given(commentRepository.findByPostId(1L)).willReturn(comments);

        // when
        List<Comment> foundComments = commentService.findCommentsByPostId(1L);

        // then
        assertThat(foundComments).hasSize(1);
        assertThat(foundComments.get(0).getPost().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("부모 댓글 ID로 대댓글 조회 테스트")
    void findRepliesByParentIdSuccess() {
        // given
        Comment reply = Comment.builder()
                .content("대댓글입니다.")
                .post(Post.builder().id(1L).build())
                .member(Member.builder().id(2L).build())
                .parentComment(Comment.builder().id(1L).build())
                .build();
        given(commentRepository.findByParentCommentId(1L)).willReturn(Arrays.asList(reply));

        // when
        List<Comment> replies = commentService.findRepliesByParentId(1L);

        // then
        assertThat(replies).hasSize(1);
        assertThat(replies.get(0).getParentComment().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("댓글 내용이 비어있을 때 예외 발생")
    void updateCommentFailEmptyContent() {
        // given
        Comment emptyComment = Comment.builder()
                .content("")
                .build();
        given(commentRepository.findById(1L)).willReturn(Optional.of(testComment));

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(1L, emptyComment))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("내용은 필수입니다.");
    }

    @Test
    @DisplayName("댓글 내용이 1000자를 초과할 때 예외 발생")
    void updateCommentFailContentTooLong() {
        // given
        String longContent = "a".repeat(1001);
        Comment longComment = Comment.builder()
                .content(longContent)
                .build();
        given(commentRepository.findById(1L)).willReturn(Optional.of(testComment));

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(1L, longComment))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("내용은 1000자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("삭제된 댓글에 답글 달기 시도시 예외 발생")
    void addReplyToDeletedCommentFail() {
        // given
        Comment deletedComment = Comment.builder()
                .content("삭제된 댓글")
                .status(CommentStatus.DELETED)
                .build();
        Comment reply = Comment.builder()
                .content("답글")
                .build();

        // when & then
        assertThatThrownBy(() -> deletedComment.addChildComment(reply))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("삭제된 댓글에는 답글을 달 수 없습니다.");
    }

    @Test
    @DisplayName("댓글 계층 구조 테스트")
    void commentHierarchyTest() {
        // given
        Post post = Post.builder()
                .id(1L)
                .name("테스트 게시글")
                .content("내용")
                .build();
                
        Comment parentComment = Comment.builder()
                .id(1L)
                .content("부모 댓글")
                .post(post)
                .build();
                
        Comment childComment = Comment.builder()
                .id(2L)
                .content("자식 댓글")
                .post(post)
                .parentComment(parentComment)
                .build();
        
        // Mock 동작 설정 수정
        given(commentRepository.save(parentComment)).willReturn(parentComment);
        given(commentRepository.save(childComment)).willReturn(childComment);

        // when
        Comment savedParent = commentService.addComment(parentComment);
        Comment savedChild = commentService.addComment(childComment);

        // then
        assertThat(savedChild.getParentComment()).isEqualTo(savedParent);
    }

    @Test
    @DisplayName("특정 게시글의 댓글 조회 시 삭제된 댓글 제외")
    void findCommentsExcludeDeletedTest() {
        // given
        Comment activeComment = Comment.builder()
                .content("활성 댓글")
                .status(CommentStatus.ACTIVE)
                .build();
        Comment deletedComment = Comment.builder()
                .content("삭제된 댓글")
                .status(CommentStatus.DELETED)
                .build();
        given(commentRepository.findByPostId(1L))
            .willReturn(List.of(activeComment));

        // when
        List<Comment> comments = commentService.findCommentsByPostId(1L);

        // then
        assertThat(comments).hasSize(1)
            .allMatch(c -> c.getStatus() != CommentStatus.DELETED);
    }
}