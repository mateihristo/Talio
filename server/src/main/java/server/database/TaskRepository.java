package server.database;

import commons.Task;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * The repository which provides CRUD operations
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
}
