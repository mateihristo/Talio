package server.api;


import com.fasterxml.jackson.databind.ObjectMapper;

import commons.Board;


import commons.BoardList;
import org.junit.jupiter.api.Test;


import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import server.database.BoardRepository;


import java.util.ArrayList;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BoardController.class)
@MockBeans({@MockBean(BoardRepository.class), @MockBean(BoardUpdateListener.class)})
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardRepository repo;

    @Autowired
    private BoardUpdateListener boardUpdateListener;

    //Checks whether invalid id is correctly handled
    @Test
    void getByIdTestError() throws Exception{
        this.mockMvc.perform(get("/api/boards/-1")).andExpect(status().isBadRequest());
    }

    @Test
    void getByIdTestCorrect() throws Exception{
        Mockito.when(this.repo.existsById((long)1)).thenReturn(true);
        Mockito.when(this.repo.findById((long)1)).thenReturn(Optional.of(new Board("test", new ArrayList<>())));
        this.mockMvc.perform(get("/api/boards/1")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void getNewBoardTest() throws Exception{
        Mockito.when(this.repo.save(Mockito.any(Board.class))).thenReturn(new Board("", new ArrayList<>()));
        String content = new ObjectMapper().writeValueAsString(new Board("", new ArrayList<>()));
        this.mockMvc.perform(
            post("/api/boards/new-board").contentType("application/json").content(content)).andExpect(status().is2xxSuccessful());
    }
    
    /**
     * Tests whether a new board is returned when there is no board in the database
     * Mocks the repository's "count" method to return 0.
     * Creates an expected board object with the name "Board Title" and an empty list of items.
     *
     * @throws Exception
     */
    @Test
    void orCreateNewTest() throws Exception{
        Mockito.when(this.repo.count()).thenReturn(0L);
        Mockito.when(this.repo.saveAndFlush(Mockito.any(Board.class))).thenAnswer(i -> i.getArguments()[0]);
        
        String content = new ObjectMapper().writeValueAsString(new Board("Board Title", new ArrayList<>()));
        
        this.mockMvc.perform(get("/api/boards/get-stored-board-or-create-new")).andExpect(status().is2xxSuccessful())
                .andExpect(content().json(content));
    }
    
    /**
     * Tests whether the correct board is returned when there is one in the database
     * Mocks the repository's "count" method to return 1.
     * Creates an expected board object with the name "Existing Board" and an empty list of items.
     *
     * @throws Exception
     */
    @Test
    void getStoredBoardTest() throws Exception {
        Mockito.when(this.repo.count()).thenReturn(1L);
        Board existingBoard = new Board("Existing Board", new ArrayList<>());
        Mockito.when(this.repo.findById(Mockito.anyLong())).thenReturn(Optional.of(existingBoard));
        
        this.mockMvc.perform(get("/api/boards/get-stored-board-or-create-new"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(existingBoard)));
    }
    
    
    @Test
    void getNewBoardTestError() throws Exception{
        String content = new ObjectMapper().writeValueAsString(new Board(null, new ArrayList<>()));
        this.mockMvc.perform(post("/api/boards/new-board").contentType("application/json").content(content)).andExpect(status().isBadRequest());

    }

    /**
     * Tests whether a new list is added to the board
     * Mocks the repository's "existsById" method to return true.
     * Mocks the repository's "findById" method to return a board with the name "test1" and an empty list of lists.
     * Mocks the repository's "save" method to return a board with the name "test1" and a list of lists with one list.
     * Creates an expected board object with the name "test1" and a list of lists with one list.
     *
     * @throws Exception
     */
    @Test
    void reorderListTestCorrect() throws Exception {
        Board board=new Board("test1",new ArrayList<>());
        ArrayList<BoardList> lists= new ArrayList<>();
        lists.add(new BoardList("name1",new ArrayList<>(),board));
        lists.add(new BoardList("name2",new ArrayList<>(),board));
        board.setLists(lists);

        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(this.repo.findById(Mockito.anyLong())).thenReturn(Optional.of(board));
        Mockito.when(this.repo.save(Mockito.any(Board.class))).thenReturn(board);
        this.mockMvc.perform(put("/api/boards/move-list/1/0/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.lists[0].name",is("name2")))
                .andExpect(jsonPath("$.lists[1].name",is("name1")));
    }

    @Test
    void reorderListTestErrorNoSuchBoard() throws Exception{
        Board board=new Board("test1",new ArrayList<>());
        ArrayList<BoardList> lists= new ArrayList<>();
        lists.add(new BoardList("name1",new ArrayList<>(),board));
        lists.add(new BoardList("name2",new ArrayList<>(),board));
        board.setLists(lists);

        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(false);
        Mockito.when(this.repo.findById(Mockito.anyLong())).thenReturn(Optional.of(board));
        Mockito.when(this.repo.save(Mockito.any(Board.class))).thenReturn(board);
        this.mockMvc.perform(put("/api/boards/move-list/1/0/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reorderListTestErrorListIndexTooBig() throws Exception{
        Board board=new Board("test1",new ArrayList<>());
        ArrayList<BoardList> lists= new ArrayList<>();
        lists.add(new BoardList("name1",new ArrayList<>(),board));
        lists.add(new BoardList("name2",new ArrayList<>(),board));
        board.setLists(lists);
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(this.repo.findById(Mockito.anyLong())).thenReturn(Optional.of(board));
        Mockito.when(this.repo.save(Mockito.any(Board.class))).thenReturn(board);
        this.mockMvc.perform(put("/api/boards/move-list/1/2/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reorderListTestErrorInvalidNewPosition() throws Exception{
        Board board=new Board("test1",new ArrayList<>());
        ArrayList<BoardList> lists= new ArrayList<>();
        lists.add(new BoardList("name1",new ArrayList<>(),board));
        lists.add(new BoardList("name2",new ArrayList<>(),board));
        board.setLists(lists);
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(this.repo.findById(Mockito.anyLong())).thenReturn(Optional.of(board));
        Mockito.when(this.repo.save(Mockito.any(Board.class))).thenReturn(board);
        this.mockMvc.perform(put("/api/boards/move-list/1/0/2"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void changeNameTestCorrect() throws Exception{
        Board board=new Board("test",new ArrayList<>());
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(this.repo.findById(Mockito.anyLong())).thenReturn(Optional.of(board));
        Mockito.when(this.repo.save(Mockito.any(Board.class))).thenReturn(board);
        this.mockMvc.perform(put("/api/boards/1/test2"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name", is("test2")));
    }
    @Test
    void changeNameTestError() throws Exception{
        Board board=new Board("test",new ArrayList<>());
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(false);
        this.mockMvc.perform(put("/api/boards/1/test2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void connectionAvailableTest() throws Exception {
        this.mockMvc.perform(get("/api/boards/connection-available"))
            .andExpect(status().isOk())
            .andExpect(content().string("Connection available"));
    }


    @Test
    void pollBoardTitleTest()throws Exception{
        Mockito.when(repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(repo.findById(Mockito.anyLong())).thenReturn(Optional.of(new Board("", new ArrayList<>())));
        Mockito.when(repo.save(Mockito.any(Board.class))).thenReturn(new Board("", new ArrayList<>()));

        MvcResult asyncListener = this.mockMvc.perform(get("/api/boards/poll-boardTitle/1")).andExpect(request().asyncStarted()).andReturn();
        this.mockMvc.perform(put("/api/boards/1/New Title"));
        String result = this.mockMvc.perform(asyncDispatch(asyncListener)).andReturn().getResponse().getContentAsString();
        assertEquals("New Title", result);

    }
}
