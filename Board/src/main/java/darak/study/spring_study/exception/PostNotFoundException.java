package darak.study.spring_study.exception;

public class PostNotFoundException extends BaseException {
    public PostNotFoundException(Long id) {
        super("게시글을 찾을 수 없습니다. ID: " + id);
    }
} 