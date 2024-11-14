package darak.study.spring_study.dto;

import darak.study.spring_study.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostPageResponse {
    private List<PostDto> posts;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private long totalPages;
}
