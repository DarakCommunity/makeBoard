package darak.study.spring_study.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PostValidator implements Validator {
    @Override
    public void validate(Object target, Errors errors) {
        Post post = (Post) target;
        
        if (post.getName() == null || post.getName().isEmpty()) {
            errors.rejectValue("name", "field.required");
        }
        
        if (post.getContent() == null || post.getContent().isEmpty()) {
            errors.rejectValue("content", "field.required");
        }
    }
} 