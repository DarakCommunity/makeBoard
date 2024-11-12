package darak.study.spring_study.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostUpdateRequest {
    private String name;
    private String content;
} 