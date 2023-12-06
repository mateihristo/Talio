package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import commons.Board;


import commons.BoardList;
import commons.Card;
import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.web.servlet.MockMvc;
import server.database.BoardListRepository;
import server.database.BoardRepository;
import server.database.CardRepository;
import server.database.TaskRepository;


import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(TaskController.class)
@MockBeans({@MockBean(TaskRepository.class), @MockBean(CardRepository.class),@MockBean(BoardListRepository.class),@MockBean(BoardRepository.class), @MockBean(BoardUpdateListener.class)})
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repo;

    @Autowired
    private CardRepository parentRepo;
    @Autowired
    private BoardListRepository listRepo;
    @Autowired
    private BoardRepository boardRepo;

    @Autowired
    private BoardUpdateListener boardUpdateListener;

    private Board testBoard;
    private BoardList testList;

    private Card testCard;
    @BeforeEach
    void setup(){
        testBoard = new Board("", new ArrayList<>());
        testList = new BoardList("",new ArrayList<>(), testBoard);
        testBoard.addList(testList);
        testCard=new Card("","",testList);
        testCard.setParentList(testList);
        testList.setParentBoard(testBoard);
        testBoard.addList(testList);
        testList.addCard(testCard);

    }

    @Test
    void getByIdTestError() throws Exception{
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(false);
        this.mockMvc.perform(get("/api/tasks/-1")).andExpect(status().isBadRequest());
    }

    @Test
    void getByIdTestCorrect() throws Exception{
        Mockito.when(repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(repo.findById(Mockito.anyLong())).thenReturn(Optional.of(new Task(testCard, "")));
        this.mockMvc.perform(get("/api/tasks/1")).andExpect(status().is2xxSuccessful());
    }

    
    @Test
    void getNewTaskTestCorrect() throws Exception{
        Mockito.when(listRepo.getById(Mockito.anyLong())).thenReturn(testList);
        Mockito.when(boardRepo.getById(Mockito.anyLong())).thenReturn(testBoard);
        Mockito.when(parentRepo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(repo.save(Mockito.any(Task.class))).thenReturn(new Task(testCard, ""));
        Mockito.when(parentRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(testCard));
        Mockito.when(boardRepo.saveAndFlush(Mockito.any(Board.class))).thenReturn(testBoard);
        Mockito.when(parentRepo.getById(Mockito.anyLong())).thenReturn(testCard);
        String content = new ObjectMapper().writeValueAsString(new Task(testCard, ""));
        this.mockMvc.perform(post("/api/tasks/new-task/1").contentType("application/json").content(content)).andExpect(status().is2xxSuccessful());
    }

    @Test
    void getNewTaskTestError() throws Exception{
        Mockito.when(parentRepo.existsById(Mockito.anyLong())).thenReturn(false);
        String content = new ObjectMapper().writeValueAsString(new Task(testCard, ""));
        this.mockMvc.perform(post("/api/tasks/new-task/-1").contentType("application/json").content(content)).andExpect(status().isBadRequest());
    }
    @Test
    void changeNameTestCorrect() throws Exception{
        Task task=new Task(testCard,"name");
        testCard.addTask(task);
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(this.repo.findById(Mockito.anyLong())).thenReturn(Optional.of(task));
        Mockito.when(this.repo.save(Mockito.any(Task.class))).thenReturn(task);
        this.mockMvc.perform(put("/api/tasks/change-name/1/newname"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name",is("newname")));
    }
    @Test
    void changeNameTestError() throws Exception{
        Task task=new Task(testCard,"name");
        testCard.addTask(task);
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(false);
        Mockito.when(this.repo.findById(Mockito.anyLong())).thenReturn(Optional.of(task));
        Mockito.when(this.repo.save(Mockito.any(Task.class))).thenReturn(task);
        this.mockMvc.perform(put("/api/tasks/change-name/1/newname"))
                .andExpect(status().isBadRequest());
        
        
    }
    
    /**
     * Tests the deleteTask method in the TaskController class
     * @throws Exception if the test fails
     */
    @Test
    void deleteTaskTestCorrect() throws Exception {
        when(repo.getById(anyLong())).thenReturn(new Task(testCard, ""));
        when(parentRepo.getById(anyLong())).thenReturn(testCard);
        when(parentRepo.saveAndFlush(any(Card.class))).thenReturn(testCard);
        this.mockMvc.perform(delete("/api/tasks/delete/1"))
                .andExpect(status().is2xxSuccessful());
    }
    
 

}
