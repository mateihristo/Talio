package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Board;
import commons.BoardList;
import commons.Card;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardListController.class)
@MockBeans({@MockBean(BoardListRepository.class), @MockBean(BoardRepository.class), @MockBean(BoardUpdateListener.class), @MockBean(CardRepository.class), @MockBean(TaskRepository.class)})
class BoardListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardListRepository repo;

    @Autowired
    private BoardRepository parentRepo;

    @Autowired
    private CardRepository cardRepo;

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private BoardUpdateListener boardUpdateListener;

    @Test
    void getByIdTestCorrect() throws Exception {
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(this.repo.findById(Mockito.anyLong())).thenReturn(Optional.of(new BoardList("", new ArrayList<>(), new Board("", new ArrayList<>()))));
        this.mockMvc.perform(get("/api/boardlists/1")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void getByIdTestError() throws Exception{
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(false);
        this.mockMvc.perform(get("/api/boardlists/-1")).andExpect(status().isBadRequest());
    }
    @Test
    void getNewListCorrect() throws Exception {
        Mockito.when(this.parentRepo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(this.parentRepo.getById(Mockito.anyLong())).thenReturn(new Board("", new ArrayList<>()));
        Mockito.when(this.repo.save(Mockito.any(BoardList.class))).thenReturn(new BoardList("", new ArrayList<>(), new Board("", new ArrayList<>())));
        Board board = new Board("", new ArrayList<>());
        BoardList list = new BoardList("", new ArrayList<>(), board);
        this.mockMvc.perform(
            post("/api/boardlists/new-boardlist/1").contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(list))).andExpect(status().is2xxSuccessful());
    }

    @Test
    void getNewListError() throws Exception{
        Mockito.when(this.parentRepo.existsById(Mockito.anyLong())).thenReturn(false);
        Board board = new Board("", new ArrayList<>());
        BoardList list = new BoardList("", new ArrayList<>(), board);
        this.mockMvc.perform(post("/api/boardlists/new-boardlist/-1").contentType("application/json").content(new ObjectMapper().writeValueAsString(list))).andExpect(status().isBadRequest());
    }

    @Test
    void renameListCorrect() throws Exception{
        Mockito.when(repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(repo.findById(Mockito.anyLong())).thenReturn(Optional.of(new BoardList("", new ArrayList<>(), new Board("", new ArrayList<>()))));
        Mockito.when(repo.save(Mockito.any(BoardList.class))).thenReturn(new BoardList("", new ArrayList<>(), new Board("", new ArrayList<>())));
        this.mockMvc.perform(put("/api/boardlists/1/newname")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void renameListError() throws Exception{
        Mockito.when(repo.existsById(Mockito.anyLong())).thenReturn(false);
        this.mockMvc.perform(put("/api/boardlists/-1/newname")).andExpect(status().isBadRequest());
    }

    @Test
    void deleteListTest() throws Exception{
        Mockito.when(repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(repo.getById(Mockito.anyLong())).thenReturn(new BoardList("", null, new Board("", null)));
        Mockito.when(parentRepo.getById(Mockito.anyLong())).thenReturn(new Board("", new ArrayList<>()));
        Mockito.when(parentRepo.saveAndFlush(Mockito.any(Board.class))).thenReturn(new Board("", null));
        mockMvc.perform(delete("/api/boardlists/1"));

    }

    @Test
    void reorderCardsTestCorrect() throws Exception {
        Board board = new Board("test1", new ArrayList<>());
        BoardList boardList=new BoardList("test",new ArrayList<>(),board);
        Card card1=new Card("card1","testcard",boardList);
        Card card2=new Card("card2","testcard",boardList);
        boardList.addCard(card1);
        boardList.addCard(card2);
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(this.repo.findById(Mockito.anyLong())).thenReturn(Optional.of(boardList));
        Mockito.when(this.repo.save(Mockito.any(BoardList.class))).thenReturn(boardList);

        this.mockMvc.perform(put("/api/boardlists/move-card/1/0/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.cardList[0].title",is("card2")))
                .andExpect(jsonPath("$.cardList[1].title",is("card1")));
    }

    @Test
    void reorderCardsTestErrorNoSuchBoardList() throws Exception {
        Board board = new Board("test1", new ArrayList<>());
        BoardList boardList=new BoardList("test",new ArrayList<>(),board);
        Card card1=new Card("card1","testcard",boardList);
        Card card2=new Card("card2","testcard",boardList);
        boardList.addCard(card1);
        boardList.addCard(card2);
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(false);
        Mockito.when(this.repo.findById(Mockito.anyLong())).thenReturn(Optional.of(boardList));
        Mockito.when(this.repo.save(Mockito.any(BoardList.class))).thenReturn(boardList);

        this.mockMvc.perform(put("/api/boardlists/move-card/1/0/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reorderCardsTestErrorCardIndexTooBig() throws Exception {
        Board board = new Board("test1", new ArrayList<>());
        BoardList boardList=new BoardList("test",new ArrayList<>(),board);
        Card card1=new Card("card1","testcard",boardList);
        Card card2=new Card("card2","testcard",boardList);
        boardList.addCard(card1);
        boardList.addCard(card2);
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(this.repo.findById(Mockito.anyLong())).thenReturn(Optional.of(boardList));
        Mockito.when(this.repo.save(Mockito.any(BoardList.class))).thenReturn(boardList);

        this.mockMvc.perform(put("/api/boardlists/move-card/1/2/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reorderCardsTestErrorInvalidNewPosition() throws Exception {
        Board board = new Board("test1", new ArrayList<>());
        BoardList boardList=new BoardList("test",new ArrayList<>(),board);
        Card card1=new Card("card1","testcard",boardList);
        Card card2=new Card("card2","testcard",boardList);
        boardList.addCard(card1);
        boardList.addCard(card2);
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(this.repo.findById(Mockito.anyLong())).thenReturn(Optional.of(boardList));
        Mockito.when(this.repo.save(Mockito.any(BoardList.class))).thenReturn(boardList);

        this.mockMvc.perform(put("/api/boardlists/move-card/1/0/2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void exchangeCardTestCorrect() throws Exception {
        Board board1=new Board("test1", new ArrayList<>());
        Board board2=new Board("test1", new ArrayList<>());
        BoardList boardList1=new BoardList("test",new ArrayList<>(),board1);
        BoardList boardList2=new BoardList("test",new ArrayList<>(),board2);
        Card card=new Card("card1","testcard",boardList1);
        boardList1.addCard(card);
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(this.repo.findById((long)1)).thenReturn(Optional.of(boardList1));
        Mockito.when(this.repo.findById((long)2)).thenReturn(Optional.of(boardList2));
        Mockito.when(this.repo.saveAndFlush(Mockito.any(BoardList.class))).thenReturn(boardList2);
        Mockito.when(cardRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(new Card("card1", "", null)));
        this.mockMvc.perform(put("/api/boardlists/exchange-card/1/2/0/0"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.cardList[0].title",is("card1")));
    }

    @Test
    void exchangeCardTestErrorNoSuchBoardList() throws Exception {
        Board board1=new Board("test1", new ArrayList<>());
        Board board2=new Board("test1", new ArrayList<>());
        BoardList boardList1=new BoardList("test",new ArrayList<>(),board1);
        BoardList boardList2=new BoardList("test",new ArrayList<>(),board2);
        Card card=new Card("card1","testcard",boardList1);
        boardList1.addCard(card);
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(false);
        Mockito.when(this.repo.findById((long)1)).thenReturn(Optional.of(boardList1));
        Mockito.when(this.repo.findById((long)2)).thenReturn(Optional.of(boardList2));
        Mockito.when(this.repo.save(Mockito.any(BoardList.class))).thenReturn(boardList2);
        this.mockMvc.perform(put("/api/boardlists/exchange-card/1/2/0/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void exchangeCardTestErrorInvalidNewPosition() throws Exception {
        Board board1=new Board("test1", new ArrayList<>());
        Board board2=new Board("test1", new ArrayList<>());
        BoardList boardList1=new BoardList("test",new ArrayList<>(),board1);
        BoardList boardList2=new BoardList("test",new ArrayList<>(),board2);
        Card card=new Card("card1","testcard",boardList1);
        boardList1.addCard(card);
        Mockito.when(this.repo.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(this.repo.findById((long)1)).thenReturn(Optional.of(boardList1));
        Mockito.when(this.repo.findById((long)2)).thenReturn(Optional.of(boardList2));
        Mockito.when(this.repo.save(Mockito.any(BoardList.class))).thenReturn(boardList2);
        Mockito.when(cardRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(new Card("card1", "", null)));
        this.mockMvc.perform(put("/api/boardlists/exchange-card/1/2/0/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getListsTest() throws Exception {
        // Create a mock board with two lists
        String name = "mockBoardName";
        List<BoardList> mockLists = new ArrayList<>();
        Board mockBoard = new Board(name, mockLists);
        String listOneName = "List 1";
        String listTwoName = "List 2";
        List<Card> cardList = new ArrayList<>();
        BoardList listOne = new BoardList(listOneName, cardList, mockBoard);
        BoardList listTwo = new BoardList(listTwoName, cardList, mockBoard);
        mockBoard.addList(listOne);
        mockBoard.addList(listTwo);
        Mockito.when(parentRepo.existsById(1L)).thenReturn(true);
        Mockito.when(parentRepo.getById(1L)).thenReturn(mockBoard);

        // Make an HTTP GET request to the endpoint with a valid board ID
        this.mockMvc.perform(get("/api/boardlists/get-all/1"))
            .andExpect(status().isOk())
            //.andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("List 1")))
            .andExpect(jsonPath("$[1].name", is("List 2")));

        // Make an HTTP GET request to the endpoint with an invalid board ID
        this.mockMvc.perform(get("/api/boardlists/get-all/-1"))
            .andExpect(status().isBadRequest());
    }
}
