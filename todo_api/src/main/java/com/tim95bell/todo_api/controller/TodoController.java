
package com.tim95bell.todo_api.controller;

import com.tim95bell.todo_api.dto.CreateTodo;
import com.tim95bell.todo_api.dto.CreateTodoForCurrentUser;
import com.tim95bell.todo_api.dto.UpdateTodo;
import com.tim95bell.todo_api.entity.Todo;
import com.tim95bell.todo_api.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Stream<Todo> findAllForUser(Authentication auth) {
        return todoService.findAllByUsername(auth.getName());
    }

    @GetMapping(params = "complete")
    @ResponseStatus(HttpStatus.OK)
    public Stream<Todo> findAllByCompleteForUser(@RequestParam boolean complete, Authentication auth) {
        return todoService.findAllByCompleteAndUsername(complete, auth.getName());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Todo findByIdForUser(@PathVariable("id") Long id, Authentication auth) {
        return todoService.findByIdAndUsername(id, auth.getName()).orElseThrow(TodoNotFoundException::new);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public Todo update(@Valid @RequestBody UpdateTodo todo, Authentication auth) {
        return todoService.updateByUsername(todo, auth.getName()).orElseThrow(TodoNotFoundException::new);
    }

    @PatchMapping(path = "/{id}", params = {"complete"})
    @ResponseStatus(HttpStatus.OK)
    public Todo complete(@PathVariable Long id, @RequestParam Boolean complete, Authentication auth) {
        return todoService.completeByIdAndUsername(id, auth.getName(), complete).orElseThrow(TodoNotFoundException::new);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo create(@Valid @RequestBody CreateTodoForCurrentUser todo, Authentication auth) {
        return todoService.create(new CreateTodo(auth.getName(), todo.text, todo.complete));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Todo deleteByIdForUser(@PathVariable Long id, Authentication auth) {
        return todoService.deleteByIdAndUsername(id, auth.getName()).orElseThrow(AdminTodoController.TodoNotFoundException::new);
    }
}
