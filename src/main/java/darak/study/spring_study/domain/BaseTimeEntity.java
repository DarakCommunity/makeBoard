package darak.study.spring_study.domain;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseTimeEntity {
    
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    
    @PrePersist
    public void prePersist() {
        this.createDate = LocalDateTime.now();
        this.updateDate = this.createDate;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updateDate = LocalDateTime.now();
    }
} 