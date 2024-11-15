package darak.study.spring_study.dto;

import darak.study.spring_study.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PostPageResponse {
    private List<PostResponse> posts;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private long totalCount;
    private long totalPages;
}
