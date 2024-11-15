package darak.study.spring_study.service;

import darak.study.spring_study.domain.Attachment;
import darak.study.spring_study.domain.Post;
import darak.study.spring_study.repository.AttachmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @InjectMocks
    private AttachmentService attachmentService;

    private Post testPost;
    private Attachment testAttachment;

    @BeforeEach
    void setUp() {
        // 테스트용 Post 객체 생성
        testPost = Post.builder()
                .id(1L)
                .name("테스트 게시글")
                .content("테스트 내용")
                .build();

        // 테스트용 Attachment 객체 생성
        testAttachment = Attachment.builder()
                .id(1L)
                .fileName("test.jpg")
                .fileType("image/jpeg")
                .fileSize(1024L)
                .post(testPost)
                .build();
    }

    @Nested
    @DisplayName("첨부파일 저장 테스트")
    class AddAttachmentTest {
        @Test
        @DisplayName("정상 저장")
        void success() {
            // given
            given(attachmentRepository.save(any(Attachment.class))).willReturn(testAttachment);
            given(attachmentRepository.findByPostId(testPost.getId())).willReturn(List.of());

            // when
            Attachment savedAttachment = attachmentService.addAttachment(testAttachment);

            // then
            assertThat(savedAttachment).isNotNull();
            assertThat(savedAttachment.getFileName()).isEqualTo("test.jpg");
            verify(attachmentRepository).save(any(Attachment.class));
        }

        @Test
        @DisplayName("게시글 없음")
        void failNoPost() {
            // given
            Attachment noPostAttachment = testAttachment.toBuilder().post(null).build();

            // when & then
            assertThatThrownBy(() -> attachmentService.addAttachment(noPostAttachment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("첨부파일은 반드시 게시글에 속해야 합니다.");
        }

        @Test
        @DisplayName("파일 개수 초과")
        void failExceedMaxCount() {
            // given
            List<Attachment> existingAttachments = Arrays.asList(
                    testAttachment, testAttachment, testAttachment, 
                    testAttachment, testAttachment
            );
            given(attachmentRepository.findByPostId(testPost.getId()))
                    .willReturn(existingAttachments);

            // when & then
            assertThatThrownBy(() -> attachmentService.addAttachment(testAttachment))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("하나의 게시글당 최대 5개의 첨부파일만 허용됩니다.");
        }
    }

    @Nested
    @DisplayName("첨부파일 유효성 검증 테스트")
    class ValidationTest {
        @Test
        @DisplayName("파일명 검증")
        void validateFileName() {
            // given
            Attachment invalidAttachment = testAttachment.toBuilder()
                    .fileName("")
                    .build();

            // when & then
            assertThatThrownBy(() -> attachmentService.addAttachment(invalidAttachment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("파일 이름은 필수입니다.");
        }

        @Test
        @DisplayName("파일 크기 검증")
        void validateFileSize() {
            // given
            Attachment oversizeAttachment = testAttachment.toBuilder()
                    .fileSize(11_000_000L)
                    .build();

            // when & then
            assertThatThrownBy(() -> attachmentService.addAttachment(oversizeAttachment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("파일 크기는 10MB를 초과할 수 없습니다.");
        }

        @Test
        @DisplayName("파일 타입 검증")
        void validateFileType() {
            // given
            Attachment invalidTypeAttachment = testAttachment.toBuilder()
                    .fileType("application/exe")
                    .build();

            // when & then
            assertThatThrownBy(() -> attachmentService.addAttachment(invalidTypeAttachment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("지원하지 않는 파일 형식입니다.");
        }
    }

    @Nested
    @DisplayName("첨부파일 조회 테스트")
    class FindAttachmentTest {
        @Test
        @DisplayName("ID로 조회")
        void findById() {
            // given
            given(attachmentRepository.findById(1L)).willReturn(Optional.of(testAttachment));

            // when
            Optional<Attachment> found = attachmentService.findAttachmentById(1L);

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getFileName()).isEqualTo("test.jpg");
        }

        @Test
        @DisplayName("게시글 ID로 조회")
        void findByPostId() {
            // given
            given(attachmentRepository.findByPostId(1L)).willReturn(List.of(testAttachment));

            // when
            List<Attachment> found = attachmentService.findAttachmentsByPostId(1L);

            // then
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getFileName()).isEqualTo("test.jpg");
        }
    }

    @Nested
    @DisplayName("첨부파일 수정 테스트")
    class UpdateAttachmentTest {
        @Test
        @DisplayName("정상 수정")
        void success() {
            // given
            Attachment updatedAttachment = testAttachment.toBuilder()
                    .fileName("updated.jpg")
                    .fileSize(2048L)
                    .build();
            given(attachmentRepository.findById(1L)).willReturn(Optional.of(testAttachment));
            given(attachmentRepository.save(any())).willReturn(updatedAttachment);

            // when
            Attachment result = attachmentService.updateAttachment(1L, updatedAttachment);

            // then
            assertThat(result.getFileName()).isEqualTo("updated.jpg");
            assertThat(result.getFileSize()).isEqualTo(2048L);
        }
    }
}