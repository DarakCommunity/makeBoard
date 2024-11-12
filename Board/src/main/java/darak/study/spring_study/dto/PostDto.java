package darak.study.spring_study.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostDto {
    private Long id;
    private String name;
    private String content;
    private Long memberId;
    private int likeCount;
    
    public static PostDto from(Post post) {
        return PostDto.builder()
            .id(post.getId())
            .name(post.getName())
            .content(post.getContent())
            .memberId(post.getMember().getId())
            .likeCount(post.getLikeCount())
            .build();
    }
} 