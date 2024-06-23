package com.app.travelboard.storage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @CreatedBy
    @Column
    private Long createdBy;

    @CreatedDate
    @Column
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column
    private Long updatedBy;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;
}
