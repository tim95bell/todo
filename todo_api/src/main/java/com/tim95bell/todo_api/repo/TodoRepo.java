
package com.tim95bell.todo_api.repo;

import com.tim95bell.todo_api.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepo extends JpaRepository<Todo, Long> {
    Optional<Todo> findByIdAndUsername(Long id, String username);
    List<Todo> findAllByComplete(boolean complete);
    List<Todo> findAllByCompleteAndUsername(boolean complete, String username);
    List<Todo> findAllByUsername(String username);
}
