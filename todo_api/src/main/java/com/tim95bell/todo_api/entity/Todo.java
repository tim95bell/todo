package com.tim95bell.todo_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
@EntityListeners(AuditingEntityListener.class)
public class Todo {
    public Todo() {}

    public Todo(String text, boolean complete) {
        this.text = text;
        this.id = null;
        this.complete = complete;
    }

    public Todo(String text) {
        this.text = text;
        this.id = null;
        this.complete = false;
    }

    @Id
    @GeneratedValue
    public Long id;
    @NotEmpty
    public String text;
    public boolean complete;
    @CreatedDate
    @Column(name = "created_date")
    public LocalDateTime createdDate;
    @LastModifiedDate
    @Column(name = "last_modified_date")
    public LocalDateTime lastModifiedDate;
}
