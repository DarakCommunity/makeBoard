package darak.study.spring_study.dto;

import darak.study.spring_study.domain.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponse {
    private Long id;
    private String name;
    private String content;
    
    public static PostResponse from(Post post) {
        return PostResponse.builder()
            .id(post.getId())
            .name(post.getName())
            .content(post.getContent())
            .build();
    }
} 