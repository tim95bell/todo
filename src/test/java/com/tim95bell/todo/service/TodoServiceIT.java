package com.tim95bell.todo.service;

import com.tim95bell.todo.TestcontainersConfiguration;
import com.tim95bell.todo.TodoApplication;
import com.tim95bell.todo.entity.Todo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(classes = {TodoApplication.class})
public class TodoServiceIT {
    @Autowired
    TodoService todoService;

    @BeforeEach
    void before() {
        assertEquals(0, todoService.findAll().count());
    }

    @AfterEach
    void after() {
        this.todoService.deleteAll();
        assertEquals(0, todoService.findAll().count());
    }

    List<Todo> addTodos(int n) {
        return LongStream.range(1, n + 1)
                .mapToObj(i -> "Test Text #" + i)
                .map(text -> {
                    Todo todo = new Todo(text);
                    todo.id = todoService.save(todo);
                    return todo;
                }).toList();
    }

    Stream<Todo> addTodos(Stream<Todo> todosToAdd) {
        return todosToAdd.map(todo -> {
            assertNull(todo.id);
            todo.id = todoService.save(todo);
            return todo;
        });
    }

    Map<Long, Todo> toMap(Stream<Todo> todos) {
        // TODO(TB): what if multiple mapped to a single id?
        return todos.collect(Collectors.toMap(x -> x.id, x -> x));
    }

    boolean equal(Stream<Todo> as, Stream<Todo> bs) {
        List<Todo> bsList = bs.toList();
        Map<Long, Todo> asMap = toMap(as);
        if (bsList.size() != asMap.size()) {
            return false;
        }

        for (final Todo b : bsList) {
            final Todo a = asMap.remove(b.id);
            if (a == null || !a.id.equals(b.id) || a.complete != b.complete || !a.text.equals(b.text)) {
                return false;
            }
        }

        return true;
    }

    @Test
    void findByExistingId() {
        final List<Todo> expectedList = addTodos(3);
        expectedList.forEach(expected -> {
            Todo result = todoService.findById(expected.id).orElseThrow();
            assertEquals(expected.id, result.id);
            assertEquals(expected.text, result.text);
            assertEquals(expected.complete, result.complete);
        });
    }

    @Test
    void findByNonExistingId() {
        final List<Todo> todoList = addTodos(3);
        final Long id = 100L;
        assertFalse(todoList.stream().anyMatch(todo -> todo.id.equals(id)));
        assertTrue(todoService.findById(id).isEmpty());
    }

    @Test
    void findByNullId() {
        addTodos(3);
        assertThrows(org.springframework.dao.InvalidDataAccessApiUsageException.class, () -> todoService.findById(null).isEmpty());
    }

    @Test
    void findAllWhenEmpty() {
        assertEquals(0, todoService.findAll().count());
    }

    @Test
    void findAllWhenOne() {
        final int count = 1;
        List<Todo> todos = addTodos(count);
        assertEquals(count, todos.size());
        List<Todo> result = todoService.findAll().toList();
        assertEquals(count, result.size());
        assertEquals(todos.getFirst().id, result.getFirst().id);
        assertEquals(todos.getFirst().text, result.getFirst().text);
        assertEquals(todos.getFirst().complete, result.getFirst().complete);
    }

    @Test
    void findAllWhenMany() {
        final int count = 5;
        List<Todo> todoList = addTodos(count).stream().toList();
        assertEquals(count, todoList.size());

        List<Todo> resultList = todoService.findAll().toList();
        assertEquals(count, resultList.size());

        assertTrue(equal(todoList.stream(), resultList.stream()));
    }

    @Test
    void findAllByCompleteWhenEmpty() {
        assertEquals(0, todoService.findAllByComplete(true).count());
    }

    @Test
    void findAllByNotCompleteWhenEmpty() {
        assertEquals(0, todoService.findAllByComplete(false).count());
    }

    @Test
    void findAllByCompleteWhenOne() {
        List<Todo> todos = addTodos(Stream.of(
            new Todo("Not Complete 1"),
            new Todo("Complete 1", true),
            new Todo("Not Complete 2")
        )).toList();

        final List<Todo> result = todoService.findAllByComplete(true).toList();
        assertEquals(1, result.size());
        assertTrue(result.getFirst().complete);
        assertTrue(equal(todos.stream().filter(x -> x.complete), result.stream()));
    }

    @Test
    void findAllByNotCompleteWhenOne() {
        List<Todo> todos = addTodos(Stream.of(
                new Todo("Complete 1", true),
                new Todo("Not Complete 1"),
                new Todo("Complete 2", true)
        )).toList();

        final List<Todo> result = todoService.findAllByComplete(false).toList();
        assertEquals(1, result.size());
        assertFalse(result.getFirst().complete);
        assertTrue(equal(todos.stream().filter(x -> !x.complete), result.stream()));
    }

    @Test
    void findAllByCompleteWhenMany() {
        List<Todo> todos = addTodos(Stream.of(
            new Todo("Not Complete 1"),
            new Todo("Complete 1", true),
            new Todo("Not Complete 2"),
            new Todo("Complete 2", true),
            new Todo("Complete 3", true),
            new Todo("Not Complete 3"),
            new Todo("Complete 4", true),
            new Todo("Complete 5", true),
            new Todo("Not Complete 4")
        )).toList();

        final List<Todo> result = todoService.findAllByComplete(true).toList();
        assertEquals(5, result.size());
        assertTrue(result.stream().allMatch(x -> x.complete));
        assertTrue(equal(todos.stream().filter(x -> x.complete), result.stream()));
    }

    @Test
    void saveNewTodoWhenEmpty() {
        Todo todo = new Todo("New Todo");
        todo.id = todoService.save(todo);
        assertNotNull(todo.id);

        assertTrue(equal(
                Stream.of(todo),
                todoService.findAll()
        ));
    }

    @Test
    void saveNewTodoWhenNotEmpty() {
        final int startCount = 5;
        List<Todo> startTodos = addTodos(startCount);
        assertEquals(startCount, todoService.findAll().count());

        Todo todo = new Todo("New Todo");
        todo.id = todoService.save(todo);
        assertNotNull(todo.id);

        List<Todo> result = todoService.findAll().toList();
        assertEquals(startCount + 1, result.size());
        assertTrue(equal(
                startTodos.stream(),
                result.stream().filter(x -> !x.id.equals(todo.id))
        ));
        assertTrue(equal(
                Stream.of(todo),
                result.stream().filter(x -> x.id.equals(todo.id))
        ));
    }

    @Test
    void saveExistingTodo() {
        final int startCount = 5;
        List<Todo> startTodos = addTodos(startCount);
        assertEquals(startCount, todoService.findAll().count());

        Todo todo = new Todo("New Text", true);
        todo.id = startTodos.get(2).id;
        assertEquals(todo.id, todoService.save(todo));

        List<Todo> result = todoService.findAll().toList();
        assertEquals(startCount, result.size());
        assertFalse(equal(
                startTodos.stream(),
                result.stream()
        ));
        assertTrue(equal(
                startTodos.stream().filter(x -> !x.id.equals(todo.id)),
                result.stream().filter(x -> !x.id.equals(todo.id))
        ));
        assertTrue(equal(
                Stream.of(todo),
                result.stream().filter(x -> x.id.equals(todo.id))
        ));
    }

    @Test
    void saveNull() {
        assertThrows(RuntimeException.class, () -> todoService.save(null));
        assertTrue(todoService.findAll().toList().isEmpty());
    }

    @Test
    void saveTodoWithEmptyText() {
        assertThrows(RuntimeException.class, () -> todoService.save(new Todo("")));
        assertTrue(todoService.findAll().toList().isEmpty());
    }

    @Test
    void saveTodoWithNullText() {
        assertThrows(RuntimeException.class, () -> todoService.save(new Todo(null)));
        assertTrue(todoService.findAll().toList().isEmpty());
    }

    @Test
    void saveTodoWithNonExistentId() {
        Todo todo = new Todo("Test Text");
        todo.id = 35L;
        assertThrows(RuntimeException.class, () -> todoService.save(todo));
        assertTrue(todoService.findAll().toList().isEmpty());
    }

    @Test
    void saveCompleted() {
        Todo todo = new Todo("Test Text", true);
        todo.id = todoService.save(todo);
        assertTrue(todo.complete);
        assertNotNull(todo.id);
        List<Todo> result = todoService.findAll().toList();
        assertTrue(equal(
                Stream.of(todo),
                result.stream()
        ));
    }

    @Test
    void saveNotCompleted() {
        Todo todo = new Todo("Test Text", false);
        todo.id = todoService.save(todo);
        assertFalse(todo.complete);
        assertNotNull(todo.id);
        List<Todo> result = todoService.findAll().toList();
        assertTrue(equal(
                Stream.of(todo),
                result.stream()
        ));
    }

    @Test
    void saveNewCreatedAndModifiedDate() {
        Todo todo = new Todo("Test Text");
        LocalDateTime before = LocalDateTime.now();
        todo.id = todoService.save(todo);
        LocalDateTime after = LocalDateTime.now();
        List<Todo> result = todoService.findAll().toList();
        assertEquals(1, result.size());
        assertEquals(todo.id, result.getFirst().id);
        assertTrue(result.getFirst().createdDate.isAfter(before));
        assertTrue(result.getFirst().createdDate.isBefore(after));
        assertEquals(result.getFirst().createdDate, result.getFirst().lastModifiedDate);
    }

    @Test
    void saveExistingCreatedAndModifiedDate() {
        Todo todo = new Todo("Test Text");
        LocalDateTime beforeCreate = LocalDateTime.now();
        todo.id = todoService.save(todo);
        LocalDateTime afterCreateBeforeModify = LocalDateTime.now();

        Todo todoAfterCreate;
        {
            List<Todo> result = todoService.findAll().toList();
            assertEquals(1, result.size());
            assertEquals(todo.id, result.getFirst().id);
            todoAfterCreate = result.getFirst();
        }

        todo.text = "New Text";
        assertEquals(todo.id, todoService.save(todo));
        LocalDateTime afterModify = LocalDateTime.now();
        List<Todo> result = todoService.findAll().toList();
        assertEquals(1, result.size());
        assertEquals(todo.id, result.getFirst().id);
        assertTrue(result.getFirst().createdDate.isAfter(beforeCreate));
        assertTrue(result.getFirst().createdDate.isBefore(afterCreateBeforeModify));
        assertTrue(result.getFirst().lastModifiedDate.isAfter(afterCreateBeforeModify));
        assertTrue(result.getFirst().lastModifiedDate.isBefore(afterModify));
        assertNotEquals(result.getFirst().createdDate, result.getFirst().lastModifiedDate);
        assertEquals(todoAfterCreate.createdDate, result.getFirst().createdDate);
        assertNotEquals(todoAfterCreate.lastModifiedDate, result.getFirst().lastModifiedDate);
    }

    @Test
    void deleteNonExistentIdWhenEmpty() {
        todoService.delete(5L);
    }

    @Test
    void deleteNonExistentIdWhenNotEmpty() {
        final int count = 5;
        addTodos(count);
        final List<Todo> todos = todoService.findAll().toList();
        assertEquals(count, todos.size());
        final Long id = todos.stream().map(x -> x.id).max(Long::compare).orElseThrow() + 1;
        assertTrue(todos.stream().noneMatch(x -> x.id.equals(id)));
        todoService.delete(id);
        assertEquals(count, todoService.findAll().count());
    }

    @Test
    void deleteNullWhenEmpty() {
        assertThrows(RuntimeException.class, () -> todoService.delete(null));
    }

    @Test
    void deleteNullWhenNotEmpty() {
        final int count = 5;
        addTodos(count);
        final List<Todo> todos = todoService.findAll().toList();
        assertEquals(count, todos.size());
        assertThrows(RuntimeException.class, () -> todoService.delete(null));
        assertEquals(count, todoService.findAll().count());
    }

    @Test
    void deleteExistingWhenOnly() {
        Long id = todoService.save(new Todo("Test Text"));
        assertNotNull(id);
        {
            List<Todo> todos = todoService.findAll().toList();
            assertEquals(1, todos.size());
            assertEquals(id, todos.getFirst().id);
        }
        todoService.delete(id);
        assertTrue(todoService.findAll().toList().isEmpty());
    }

    @Test
    void deleteExistingWhenMany() {
        final int startCount = 5;
        List<Todo> todos = addTodos(startCount);
        assertEquals(startCount, todos.size());
        final Long id = todos.get(2).id;
        assertNotNull(id);
        {
            List<Todo> result = todoService.findAll().toList();
            assertEquals(startCount, result.size());
            assertEquals(1, result.stream().filter(x -> x.id.equals(id)).count());
        }
        todoService.delete(id);
        List<Todo> result = todoService.findAll().toList();
        assertEquals(startCount - 1, result.size());
        assertTrue(equal(
                todos.stream().filter(x -> !x.id.equals(id)),
                result.stream()
        ));
        assertFalse(result.stream().anyMatch(x -> x.id.equals(id)));
    }

    @Test
    void deleteAllWhenEmpty() {
        todoService.deleteAll();
        assertTrue(todoService.findAll().toList().isEmpty());
    }

    @Test
    void deleteAllWhenOne() {
        addTodos(1);
        assertEquals(1, todoService.findAll().count());
        todoService.deleteAll();
        assertTrue(todoService.findAll().toList().isEmpty());
    }

    @Test
    void deleteAllWhenMany() {
        final int startCount = 5;
        addTodos(startCount);
        assertEquals(startCount, todoService.findAll().count());
        todoService.deleteAll();
        assertTrue(todoService.findAll().toList().isEmpty());
    }
}
