package com.tim95bell.todo_api.repo;

import com.tim95bell.todo_api.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepo extends JpaRepository<Todo, Long> {
    List<Todo> findAllByComplete(boolean complete);
}
