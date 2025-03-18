
package com.tim95bell.todo_api.controller;

import com.tim95bell.todo_api.dto.CreateTodo;
import com.tim95bell.todo_api.dto.UpdateTodo;
import com.tim95bell.todo_api.entity.Todo;
import com.tim95bell.todo_api.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

@RestController
@RequestMapping("/api/admin/todo")
public class AdminTodoController {
    private final TodoService todoService;

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No TODO with given ID")
    static class TodoNotFoundException extends RuntimeException {}

    public AdminTodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Stream<Todo> findAll() {
        return todoService.findAll();
    }

    @GetMapping(params = "complete")
    @ResponseStatus(HttpStatus.OK)
    public Stream<Todo> findAllByComplete(@RequestParam boolean complete) {
        return todoService.findAllByComplete(complete);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Todo findById(@PathVariable("id") Long id) {
        return todoService.findById(id).orElseThrow(TodoController.TodoNotFoundException::new);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public Todo update(@RequestBody UpdateTodo todo) {
        return todoService.update(todo).orElseThrow(TodoNotFoundException::new);
    }

    @PatchMapping(path = "/{id}", params = {"complete"})
    @ResponseStatus(HttpStatus.OK)
    public Todo complete(@PathVariable Long id, @RequestParam Boolean complete) {
        return todoService.completeById(id, complete).orElseThrow(TodoNotFoundException::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Todo create(@Valid @RequestBody CreateTodo todo) {
        return todoService.create(todo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Todo deleteById(@PathVariable Long id) {
        return todoService.deleteById(id).orElseThrow(TodoNotFoundException::new);
    }
}
