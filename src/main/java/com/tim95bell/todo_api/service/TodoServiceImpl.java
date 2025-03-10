package com.tim95bell.todo_api.service;

import com.tim95bell.todo_api.entity.Todo;
import com.tim95bell.todo_api.repo.TodoRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class TodoServiceImpl implements TodoService {
    TodoRepo todoRepo;

    public TodoServiceImpl(TodoRepo todoRepo) {
        this.todoRepo = todoRepo;
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return todoRepo.findById(id);
    }

    @Override
    public Stream<Todo> findAll() {
        return todoRepo.findAll().stream();
    }

    @Override
    public Stream<Todo> findAllByComplete(boolean complete) {
        return todoRepo.findAllByComplete(complete).stream();
    }

    @Override
    public Long save(Todo todo) {
        return todoRepo.save(todo).id;
    }

    @Override
    public void delete(Long id) {
        todoRepo.deleteById(id);
    }

    @Override
    public void deleteAll() {
        todoRepo.deleteAll();
    }
}
