package com.tim95bell.todo_api.controller;

import com.tim95bell.todo_api.entity.Todo;
import com.tim95bell.todo_api.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

@RestController
@RequestMapping("/api/todo")
public class TodoController {
    private final TodoService todoService;

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No TODO with given ID")
    static class TodoNotFoundException extends RuntimeException {}

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping(params = "complete")
    @ResponseStatus(HttpStatus.OK)
    public Stream<Todo> findAllByComplete(@RequestParam boolean complete) {
        return todoService.findAllByComplete(complete);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Stream<Todo> findAll() {
        return todoService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Todo findById(@PathVariable("id") Long id) {
        return todoService.findById(id).orElseThrow(TodoNotFoundException::new);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        todoService.delete(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void create(@Valid @RequestBody Todo todo) {
        todoService.save(todo);
    }

    @PatchMapping(path = "/{id}", params = {"complete"})
    @ResponseStatus(HttpStatus.OK)
    public void complete(@PathVariable Long id, @RequestParam Boolean complete) {
        todoService.findById(id).ifPresentOrElse(todo -> {
            todo.complete = complete;
            todoService.save(todo);
        }, () -> {
            throw new TodoNotFoundException();
        });
    }
}
