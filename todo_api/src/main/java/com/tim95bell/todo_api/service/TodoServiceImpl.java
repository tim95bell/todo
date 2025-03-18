
package com.tim95bell.todo_api.service;

import com.tim95bell.todo_api.dto.CreateTodo;
import com.tim95bell.todo_api.dto.UpdateTodo;
import com.tim95bell.todo_api.entity.Todo;
import com.tim95bell.todo_api.repo.TodoRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
public class TodoServiceImpl implements TodoService {
    TodoRepo todoRepo;

    public TodoServiceImpl(TodoRepo todoRepo) {
        this.todoRepo = todoRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<Todo> findAll() {
        return todoRepo.findAll().stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<Todo> findAllByUsername(String username) {
        return todoRepo.findAllByUsername(username).stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<Todo> findAllByComplete(boolean complete) {
        return todoRepo.findAllByComplete(complete).stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<Todo> findAllByCompleteAndUsername(boolean complete, String username) {
        return todoRepo.findAllByCompleteAndUsername(complete, username).stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Todo> findById(Long id) {
        return todoRepo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Todo> findByIdAndUsername(Long id, String username) {
        return todoRepo.findByIdAndUsername(id, username);
    }

    @Override
    public Optional<Todo> update(UpdateTodo todo) {
        return todoRepo.findById(todo.id).map(x -> {
            x.text = todo.text;
            x.complete = todo.complete;
            return x;
        });
    }

    @Override
    public Optional<Todo> updateByUsername(UpdateTodo todo, String username) {
        return todoRepo.findByIdAndUsername(todo.id, username).map(x -> {
            x.text = todo.text;
            x.complete = todo.complete;
            return x;
        });
    }

    @Override
    public Optional<Todo> completeById(Long id, boolean complete) {
        return todoRepo.findById(id).map(x -> {
            x.complete = complete;
            return x;
        });
    }

    @Override
    public Optional<Todo> completeByIdAndUsername(Long id, String username, boolean complete) {
        return todoRepo.findByIdAndUsername(id, username).map(x -> {
            x.complete = complete;
            return x;
        });
    }

    @Override
    public Todo create(CreateTodo todo) {
        return todoRepo.save(new Todo(todo.text, todo.complete, todo.username));
    }

    @Override
    public Optional<Todo> deleteById(Long id) {
        return todoRepo.findById(id).map(x -> {
            todoRepo.deleteById(id);
            return x;
        });
    }

    @Override
    public Optional<Todo> deleteByIdAndUsername(Long id, String username) {
        return todoRepo.findByIdAndUsername(id, username).map(x -> {
            todoRepo.deleteById(id);
            return x;
        });
    }

    @Override
    public void deleteAll() {
        todoRepo.deleteAll();
    }
}
