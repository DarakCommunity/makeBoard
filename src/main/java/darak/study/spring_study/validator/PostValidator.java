package darak.study.spring_study.validator;

import darak.study.spring_study.domain.Post;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PostValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

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

    @Override
    public Errors validateObject(Object target) {
        return Validator.super.validateObject(target);
    }
} 