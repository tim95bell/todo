package com.tim95bell.todo.service;

import com.tim95bell.todo.entity.Todo;

import java.util.Optional;
import java.util.stream.Stream;

public interface TodoService {
    Optional<Todo> findById(Long id);
    Stream<Todo> findAll();
    Stream<Todo> findAllByComplete(boolean complete);
    Long save(Todo todo);
    void delete(Long id);
    void deleteAll();
}
