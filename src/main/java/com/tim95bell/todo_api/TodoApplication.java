package com.tim95bell.todo_api;

import com.tim95bell.todo_api.entity.Todo;
import com.tim95bell.todo_api.service.TodoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.stream.Stream;

@SpringBootApplication
@EnableJpaAuditing
public class TodoApplication {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(TodoApplication.class, args);
		TodoService todoService = context.getBean(TodoService.class);
		for (int i = 0; i < 100; ++i) {
			todoService.save(new Todo("TODO #" + (i + 1)));
		}
		Stream<Todo> todos = todoService.findAll();
		assert(todos.count() == 100);
	}
}
