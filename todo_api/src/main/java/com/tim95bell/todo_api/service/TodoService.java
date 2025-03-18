
package com.tim95bell.todo_api.service;

import com.tim95bell.todo_api.dto.CreateTodo;
import com.tim95bell.todo_api.dto.UpdateTodo;
import com.tim95bell.todo_api.entity.Todo;

import java.util.Optional;
import java.util.stream.Stream;

public interface TodoService {
    Stream<Todo> findAll();
    Stream<Todo> findAllByUsername(String username);
    Stream<Todo> findAllByComplete(boolean complete);
    Stream<Todo> findAllByCompleteAndUsername(boolean complete, String username);
    Optional<Todo> findById(Long id);
    Optional<Todo> findByIdAndUsername(Long id, String username);
    Optional<Todo> update(UpdateTodo todo);
    Optional<Todo> updateByUsername(UpdateTodo todo, String username);
    Optional<Todo> completeById(Long id, boolean complete);
    Optional<Todo> completeByIdAndUsername(Long id, String username, boolean complete);
    Todo create(CreateTodo todo);
    Optional<Todo> deleteById(Long id);
    Optional<Todo> deleteByIdAndUsername(Long id, String username);
    void deleteAll();
}
