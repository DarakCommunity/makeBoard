package darak.study.spring_study.exception;

public class MemberNotFoundException extends BaseException {
    public MemberNotFoundException(Long id) {
        super("회원을 찾을 수 없습니다. ID: " + id);
    }
    
    public MemberNotFoundException(String email) {
        super("회원을 찾을 수 없습니다. Email: " + email);
    }
} 