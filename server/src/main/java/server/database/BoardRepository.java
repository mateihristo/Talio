package server.database;

import commons.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT MIN(id) FROM Board")
    Long getMinId();
}
