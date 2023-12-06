package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import commons.Board;
import commons.BoardList;
import commons.Card;

import commons.Task;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;


@WireMockTest(httpPort = 8080)
class ServerUtilsTest {


    @Test
    void getCardTest() throws Exception{
        Board board = new Board("", new ArrayList<>());
        BoardList list = new BoardList("", new ArrayList<>(), board);
        stubFor(get("/api/cards/33").willReturn(
            aResponse().withHeader("Content-Type","application/json").withBody(new ObjectMapper().writeValueAsString(new Card("test", "", list))))
            );
        ServerUtils server = new ServerUtils();
        Card card = server.getCard(33);
        assertNotNull(card);
        assertEquals("test", card.getTitle());
    }

    @Test
    void getBoardTest() throws Exception{
        Board testBoard = new Board("testname", new ArrayList<>());
        stubFor(get("/api/boards/129").willReturn(
            aResponse().withHeader("Content-Type", "application/json").withBody(new ObjectMapper().writeValueAsString(testBoard))
        ));
        ServerUtils server = new ServerUtils();
        Board board = server.getBoard(129);
        assertNotNull(board);
        assertEquals("testname", board.getName());
    }
    

    @Test
    void postNewCardTest() throws Exception{
        Board board = new Board("", new ArrayList<>());
        BoardList list = new BoardList("", new ArrayList<>(), board);
        list.id = 1;
        Card testCard = new Card("title", "desc", list);
        stubFor(post("/api/cards/new-card/1").willReturn(
            aResponse().withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(testCard))
        ));
        ServerUtils server = new ServerUtils();
        Card card = server.postNewCard(testCard, list);
        assertNotNull(card);
        assertEquals("title", card.getTitle());
        assertEquals("desc", card.getDescription());
    }
    
    /**
     * Tests the behavior of the deleteCard method in the ServerUtils class when
     * called with a valid card to delete.
     * @throws Exception is there is an error with the test
     */
    @Test
    void deleteCardTest() throws Exception {
        Board board = new Board("", new ArrayList<>());
        Card cardToDelete = new Card("testname", "bla", new BoardList("", new ArrayList<>(), board));
        cardToDelete.id = 1;
        stubFor(delete("api/cards/delete/1").willReturn(
                aResponse().withHeader("Content-Type", "application/json").withBody(new ObjectMapper().writeValueAsString(cardToDelete))
        ));
        ServerUtils server = new ServerUtils();
        assertDoesNotThrow(() -> server.deleteCard(cardToDelete));
        
    }
    @Test
    void postNewBoardTest() throws Exception{
        Board boardToPost = new Board("testname", new ArrayList<>());
        stubFor(post("/api/boards/new-board").willReturn(
            aResponse().withHeader("Content-Type", "application/json").withBody(new ObjectMapper().writeValueAsString(boardToPost))
        ));
        ServerUtils server = new ServerUtils();
        Board board = new Board("test", new ArrayList<>());
        Board createdBoard = server.postNewBoard(board);
        assertNotNull(createdBoard);
        assertEquals("testname", createdBoard.getName());
        assertEquals(new ArrayList<BoardList>(),createdBoard.getLists() );
    }
    
    /**
     * Test to verify if the renaming of a board works
     * Sends a put request to the server with the specified id + new name of the board
     * Checks if the response of the server is the board with the expected new name
     * @throws Exception if test fails
     */
    @Test
    void renameBoard() throws Exception{
        Board board = new Board("newname", new ArrayList<>());
        board.id = 1;
        stubFor(put("/api/boards/1/newname").willReturn(
                aResponse().withHeader("Content-Type", "application/json").withBody(new ObjectMapper().writeValueAsString(board))));
        ServerUtils server = new ServerUtils();
        Board changedBoard = server.renameBoard(board);
        assertNotNull(changedBoard);
        assertEquals("newname", changedBoard.getName());
    }
    
    @Test
    void renameCard() throws Exception{
        Board board = new Board("", new ArrayList<>());
        Card card = new Card("newname", "bla", new BoardList("", new ArrayList<>(), board));
        card.id = 1;
        stubFor(put("/api/cards/1/newname").willReturn(
                aResponse().withHeader("Content-Type", "application/json").withBody(new ObjectMapper().writeValueAsString(card))));
        ServerUtils server = new ServerUtils();
        Card changedCard = server.renameCard(card);
        assertNotNull(changedCard);
        assertEquals("newname", changedCard.getTitle());
    }

    @Test
    void getListTest() throws Exception{
        BoardList list = new BoardList("", new ArrayList<>(), new Board("", new ArrayList<>()));
        stubFor(get("/api/boardlists/1").willReturn(
            aResponse().withHeader("Content-Type", "application/json").withBody(new ObjectMapper().writeValueAsString(list))
        ));
        ServerUtils server = new ServerUtils();
        BoardList receivedList =  server.getList(1);
        assertNotNull(receivedList);
        assertEquals("", receivedList.getName());
        assertEquals(new ArrayList<Card>(), receivedList.getCardList());
    }
    @Test
    void getListsTest() throws Exception{
        Board board = new Board("", new ArrayList<BoardList>());
        board.id= 1;
        board.addList(new BoardList("empty", new ArrayList<Card>()));
        stubFor(get("/api/boardlists/get-all/1").willReturn(
           aResponse().withHeader("Content-Type","application/json").withBody(new ObjectMapper().writeValueAsString(board.getLists()))
        ));
        ServerUtils server = new ServerUtils();
        List<BoardList> receivedLists = server.getLists(board);
        assertNotNull(receivedLists);
        assertEquals(receivedLists.get(0).getName(),"empty");

    }
    
    @Test
    void postListTest() throws Exception{
        Board board = new Board("", new ArrayList<>());
        BoardList list = new BoardList("", new ArrayList<>(), board);
        board.id = 1;
        stubFor(post("/api/boardlists/new-boardlist/1").willReturn(
            aResponse().withHeader("Content-Type", "application/json").withBody(new ObjectMapper().writeValueAsString(list))));
        ServerUtils server = new ServerUtils();
        BoardList createdList = server.postNewList(list,board);
        assertNotNull(createdList);
        assertEquals("", createdList.getName());
        assertEquals(new ArrayList<Card>(), createdList.getCardList());
    }

    @Test
    void renameListTest() throws Exception{
        Board board = new Board("", new ArrayList<>());
        BoardList list = new BoardList("newname", new ArrayList<>(), board);
        list.id = 1;
        stubFor(put("/api/boardlists/1/newname").willReturn(
            aResponse().withHeader("Content-Type", "application/json").withBody(new ObjectMapper().writeValueAsString(list))));
        ServerUtils server = new ServerUtils();
        BoardList changedList = server.renameList(list, board);
        assertNotNull(changedList);
        assertEquals("newname", changedList.getName());
        assertEquals(new ArrayList<Card>(), changedList.getCardList());
    }
    

    
    @Test
    void deleteListTest() throws Exception{
        Board board = new Board("", new ArrayList<>());
        BoardList list = new BoardList("", new ArrayList<>(), board);
        list.id = 1;
        stubFor(delete("api/boardlists/1").willReturn(
            aResponse().withHeader("Content-Type", "application/json").withBody(new ObjectMapper().writeValueAsString(list))
        ));
        ServerUtils server = new ServerUtils();
        assertDoesNotThrow(() -> server.deleteList(list));

    }





    @Test
    void pollBoardTitleTest(){
        Board board = new Board("", new ArrayList<>());
        board.id = 1;
        stubFor(get("/api/boards/poll-boardTitle/1").willReturn(
            aResponse().withHeader("Content-Type", "application/json").withBody("New Title")
        ));
        ServerUtils server = new ServerUtils();
        server.pollBoardTitle(1, p -> {
            assertEquals("New Title", p);
        });
    }


    @Test
    void getCardTestExtended() throws Exception{
        String title = "title";
        String description = "description";
        String name = "name";
        List<Card> cardList = new ArrayList<>();
        BoardList parentList = new BoardList(name, cardList);
        Card card = new Card(title, description, parentList);
        parentList.addCard(card);
        card.id = 1;
        stubFor(get("/api/cards/1")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(card))
        ));
        ServerUtils serverUtils = new ServerUtils();
        Card gottenCard = serverUtils.getCard(1);
        String wrong = "wrong";
        assertDoesNotThrow(() -> serverUtils.getCard(1));
        assertThrows(Exception.class, () -> serverUtils.getCard(2));
        assertNotNull(gottenCard);
        assertEquals(title, gottenCard.getTitle());
        assertEquals(description, gottenCard.getDescription());
        assertEquals(1, gottenCard.id);
        assertNotEquals(wrong, gottenCard.getTitle());
        assertNotEquals(wrong, gottenCard.getDescription());
        assertNotEquals(2, gottenCard.id);
    }

    @Test
    void postNewCardTestExtended() throws Exception{
        String title = "title";
        String description = "description";
        Card newCard = new Card(title, description, null);
        String name = "name";
        List<Card> cardList = new ArrayList<>();
        BoardList parentBoardList = new BoardList(name, cardList);
        parentBoardList.id = 1;
        stubFor(post("/api/cards/new-card/1")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(newCard))
            ));
        ServerUtils serverUtils = new ServerUtils();
        Card postedCard = serverUtils.postNewCard(newCard, parentBoardList);
        long postedCardParentListId = postedCard.getParentList().id;
        long wrongId = 2;
        assertDoesNotThrow(() -> serverUtils.postNewCard(newCard, parentBoardList));
        assertThrows(Exception.class, () -> serverUtils.postNewCard(newCard, null));
        assertNotNull(postedCard);
        assertEquals(parentBoardList.id, postedCardParentListId);
        assertNotEquals(wrongId, postedCardParentListId);
    }

    @Test
    void updateCardDescriptionTestException() throws Exception{
        String title = "title";
        String description = "description";
        String newDescription = "new-description";
        String name = "name";
        List<Card> cardList = new ArrayList<>();
        BoardList parentList = new BoardList(name, cardList);
        Card card = new Card(title, description, parentList);
        Card newCard = new Card(title, newDescription, parentList);
        parentList.addCard(newCard);
        newCard.id = 1;
        stubFor(put("/api/cards/change-description/1/new-description")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(newCard))));
        ServerUtils serverUtils = new ServerUtils();
        assertDoesNotThrow(() -> serverUtils.updateCardDescription(newCard));
        assertThrows(Exception.class, () -> serverUtils.updateCardDescription(null));
    }

    @Test
    void updateCardDescriptionTestNotNull() throws Exception{
        String title = "title";
        String description = "description";
        String newDescription = "new-description";
        String name = "name";
        List<Card> cardList = new ArrayList<>();
        BoardList parentList = new BoardList(name, cardList);
        Card card = new Card(title, description, parentList);
        Card newCard = new Card(title, newDescription, parentList);
        parentList.addCard(newCard);
        newCard.id = 1;
        stubFor(put("/api/cards/change-description/1/new-description")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(newCard))));
        ServerUtils serverUtils = new ServerUtils();
        card = serverUtils.updateCardDescription(newCard);
        assertNotNull(card);
    }

    @Test
    void updateCardDescriptionTestEquals() throws Exception{
        String title = "title";
        String description = "description";
        String newDescription = "new-description";
        String name = "name";
        List<Card> cardList = new ArrayList<>();
        BoardList parentList = new BoardList(name, cardList);
        Card card = new Card(title, description, parentList);
        Card newCard = new Card(title, newDescription, parentList);
        parentList.addCard(newCard);
        newCard.id = 1;
        stubFor(put("/api/cards/change-description/1/new-description")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(newCard))));
        ServerUtils serverUtils = new ServerUtils();
        card = serverUtils.updateCardDescription(newCard);
        assertEquals(newDescription, card.getDescription());
        assertNotEquals(description, card.getDescription());
    }

    @Test
    void getListTestExtended() throws Exception{
        String name = "name";
        List<Card> cardList = new ArrayList<>();
        BoardList list = new BoardList(name, cardList);
        list.id = 1;
        stubFor(get("/api/boardlists/1")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(list))));
        ServerUtils serverUtils = new ServerUtils();
        BoardList gottenList = serverUtils.getList(1);
        String wrongName = "wrongName";
        long wrongId = 2;
        assertDoesNotThrow(() -> serverUtils.getList(1));
        assertThrows(Exception.class, () -> serverUtils.getList(0));
        assertNotNull(gottenList);
        assertEquals(name, gottenList.getName());
        assertEquals(1, gottenList.id);
        assertNotEquals(wrongName, gottenList.getName());
        assertNotEquals(wrongId, gottenList.id);
    }

    @Test
    void postNewListTestExtended() throws Exception{
        String name = "name";
        List<Card> cardList = new ArrayList<>();
        BoardList newBoardList = new BoardList(name, cardList);
        String boardName = "boardName";
        List<BoardList> lists = new ArrayList<>();
        Board parent = new Board(boardName, lists);
        parent.id = 1;
        stubFor(post("/api/boardlists/new-boardlist/1")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(newBoardList))));
        ServerUtils serverUtils = new ServerUtils();
        BoardList postedList = serverUtils.postNewList(newBoardList, parent);
        String wrong = "wrong";
        assertDoesNotThrow(() -> serverUtils.postNewList(newBoardList, parent));
        assertThrows(Exception.class, () -> serverUtils.postNewList(newBoardList, null));
        assertNotNull(postedList);
        assertEquals(newBoardList.getName(), postedList.getName());
        assertEquals(parent.getName(), postedList.getParentBoard().getName());
        assertEquals(1, postedList.getParentBoard().id);
        assertNotEquals(wrong, postedList.getName());
        assertNotEquals(wrong, postedList.getParentBoard().getName());
        assertNotEquals(0, postedList.getParentBoard().id);
    }

    @Test
    void renameListTestExtended() throws Exception{
        String oldName = "oldName";
        List<Card> cardList = new ArrayList<>();
        String boardName = "boardName";
        List<BoardList> lists = new ArrayList<>();
        Board parent = new Board(boardName, lists);
        BoardList oldList = new BoardList(oldName, cardList, parent);
        parent.addList(oldList);
        String newName = "newName";
        BoardList changedList = new BoardList(newName, cardList, parent);
        changedList.id = 1;
        stubFor(put("/api/boardlists/1/newName")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(changedList))));
        ServerUtils serverUtils = new ServerUtils();
        BoardList renamedList = serverUtils.renameList(changedList, parent);
        assertDoesNotThrow(() -> serverUtils.renameList(changedList, parent));
        assertNotNull(renamedList);
        assertEquals(oldList.getParentBoard().getName(), renamedList.getParentBoard().getName());
        assertEquals(changedList.getName(), renamedList.getName());
        assertNotEquals(oldList.getName(), renamedList.getName());
    }

    @Test
    void getBoardTestExtended() throws Exception{
        String name = "name";
        List<BoardList> lists = new ArrayList<>();
        Board board = new Board(name, lists);
        String listName = "listName";
        List<Card> cardList = new ArrayList<>();
        BoardList list = new BoardList(listName, cardList, board);
        board.addList(list);
        String title = "title";
        String description = "description";
        Card card = new Card(title, description, list);
        list.addCard(card);
        board.id = 1;
        stubFor(get("/api/boards/1")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(board))));
        ServerUtils serverUtils = new ServerUtils();
        Board gottenBoard = serverUtils.getBoard(1);
        String wrongName = "wrongName";
        long wrongId = 2;
        assertDoesNotThrow(() -> serverUtils.getBoard(1));
        assertThrows(Exception.class, () -> serverUtils.getBoard(0));
        assertNotNull(gottenBoard);
        assertEquals(board.getName(), gottenBoard.getName());
        assertEquals(board.id, gottenBoard.id);
        assertNotEquals(wrongName, gottenBoard.getName());
        assertNotEquals(wrongId, gottenBoard.id);
    }

    @Test
    void postNewBoardTestExtended() throws Exception{
        String name = "name";
        List<BoardList> lists = new ArrayList<>();
        Board newBoard = new Board(name, lists);
        stubFor(post("/api/boards/new-board")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(newBoard))));
        ServerUtils serverUtils = new ServerUtils();
        Board postedBoard = serverUtils.postNewBoard(newBoard);
        String wrongName = "wrongName";
        assertDoesNotThrow(() -> serverUtils.postNewBoard(newBoard));
        assertNotNull(postedBoard);
        assertEquals(newBoard.getName(), postedBoard.getName());
        assertNotEquals(wrongName, postedBoard.getName());
    }

    @Test
    void renameBoardTestExtended() throws Exception{
        String oldName = "oldName";
        List<BoardList> lists = new ArrayList<>();
        Board oldBoard = new Board(oldName, lists);
        String newName = "newName";
        Board changedBoard = new Board(newName, lists);
        changedBoard.id = 1;
        stubFor(put("/api/boards/1/newName")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(changedBoard))));
        ServerUtils serverUtils = new ServerUtils();
        Board renamedBoard = serverUtils.renameBoard(changedBoard);
        assertDoesNotThrow(() -> serverUtils.renameBoard(changedBoard));
        assertThrows(Exception.class, () -> serverUtils.renameBoard(null));
        assertNotNull(renamedBoard);
        assertEquals(changedBoard.getName(), renamedBoard.getName());
        assertEquals(1, renamedBoard.id);
        assertNotEquals(oldBoard.getName(), renamedBoard.getName());
        assertNotEquals(2, renamedBoard.id);
    }

    @Test
    void dropCardOnOtherListTestDoesNotThrow() throws Exception{
        String title = "title";
        String description = "description";
        String name = "name";
        List<Card> cardList = new ArrayList<>();
        BoardList parentList = new BoardList(name, cardList);
        Card movedCard = new Card(title, description, parentList);
        parentList.addCard(movedCard);
        long newParentId = 1;
        long newPos = 2;
        movedCard.getParentList().id = 3;
        movedCard.id = 4;
        stubFor(put("/api/boardlists/exchange-card/3/1/4/2")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(movedCard))));
        ServerUtils serverUtils = new ServerUtils();
        assertDoesNotThrow(() -> serverUtils.dropCardOnOtherList(movedCard, newParentId, newPos));
    }

    @Test
    void dropCardOnOtherListTestThrows() throws Exception{
        String title = "title";
        String description = "description";
        String name = "name";
        List<Card> cardList = new ArrayList<>();
        BoardList parentList = new BoardList(name, cardList);
        Card movedCard = new Card(title, description, parentList);
        parentList.addCard(movedCard);
        long newParentId = 1;
        long newPos = 2;
        movedCard.getParentList().id = 3;
        movedCard.id = 4;
        stubFor(put("/api/boardlists/exchange-card/3/1/4/2")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(movedCard))));
        ServerUtils serverUtils = new ServerUtils();
        assertThrows(Exception.class, () -> serverUtils.dropCardOnOtherList(null, newParentId, newPos));
    }

    @Test
    void getBoardOrCreateNewTestGetBoardNoException() throws Exception{
        String name = "name";
        List<BoardList> lists = new ArrayList<>();
        Board board = new Board(name, lists);
        stubFor(get("/api/boards/get-stored-board-or-create-new")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(board))));
        ServerUtils serverUtils = new ServerUtils();
        assertDoesNotThrow(serverUtils::getBoardOrCreateNew);
    }

    @Test
    void getBoardOrCreateNewTestGetBoardNotNull() throws Exception{
        String name = "name";
        List<BoardList> lists = new ArrayList<>();
        Board board = new Board(name, lists);
        stubFor(get("/api/boards/get-stored-board-or-create-new")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(board))));
        ServerUtils serverUtils = new ServerUtils();
        Board gottenBoard = serverUtils.getBoardOrCreateNew();
        assertNotNull(gottenBoard);
    }

    @Test
    void getBoardOrCreateNewTestGetBoardEquals() throws Exception{
        String name = "name";
        List<BoardList> lists = new ArrayList<>();
        Board board = new Board(name, lists);
        stubFor(get("/api/boards/get-stored-board-or-create-new")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(board))));
        ServerUtils serverUtils = new ServerUtils();
        Board gottenBoard = serverUtils.getBoardOrCreateNew();
        String wrongName = "wrongName";
        assertEquals(board.getName(), gottenBoard.getName());
        assertNotEquals(wrongName, gottenBoard.getName());
    }

    @Test
    void getBoardOrCreateNewTestCreateNewNoException() throws Exception{
        Board board = new Board();
        stubFor(get("/api/boards/get-stored-board-or-create-new")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(board))));
        ServerUtils serverUtils = new ServerUtils();
        assertDoesNotThrow(serverUtils::getBoardOrCreateNew);
    }

    @Test
    void getBoardOrCreateNewTestCreateNewNotNull() throws Exception{
        Board board = new Board();
        stubFor(get("/api/boards/get-stored-board-or-create-new")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(new ObjectMapper().writeValueAsString(board))));
        ServerUtils serverUtils = new ServerUtils();
        Board gottenBoard = serverUtils.getBoardOrCreateNew();
        assertNotNull(gottenBoard);
    }

    
    /*
    * Test to verify if you get the correct Task from the server
    * Send a get request to the server based on id
     */
    @Test
    void getTaskTest() throws Exception{
        Board board = new Board("", new ArrayList<>());
        BoardList list = new BoardList("", new ArrayList<>(), board);
        list.id = 1;
        Card card = new Card("", "desc", list);
        Task task = new Task(card, "title");
        
        stubFor(get("/api/tasks/1").willReturn(
                aResponse().withHeader("Content-Type", "application/json").withBody(new ObjectMapper().writeValueAsString(task))
        ));
        ServerUtils server = new ServerUtils();
        Task receivedTask =  server.getTask(1);
        receivedTask.setParentCard(card);
        assertNotNull(receivedTask);
        assertEquals("title", receivedTask.getName());
        assertEquals(task, receivedTask);
    }
    
    /**
     * Test to verify if the creation of a new task works
     * Test to check if creating of new tasks work
     * By sending a post request
     * @throws Exception if test fails
     */
    @Test
    void postNewTaskTest() throws Exception{
        Board board = new Board("", new ArrayList<>());
        BoardList list = new BoardList("", new ArrayList<>(), board);
        Card card = new Card("title", "desc", list);
        card.id = 1;
        Task testTask = new Task(card, "title");
        stubFor(post("/api/tasks/new-task/1").willReturn(
                aResponse().withHeader("Content-Type", "application/json")
                        .withBody(new ObjectMapper().writeValueAsString(testTask))
        ));
        ServerUtils server = new ServerUtils();
        Task task = server.postNewTask(testTask, card);
        assertNotNull(task);
        assertEquals("title", task.getName());
        assertEquals(card, task.getParentCard());
    }
    
    /**
     * Test to verify if deleting a task works
     * @throws Exception if test fails
     */
    @Test
    void deleteTaskTest() throws Exception{
        Board board = new Board("", new ArrayList<>());
        BoardList list = new BoardList("", new ArrayList<>(), board);
        Card card = new Card("title", "desc", list);
        Task task = new Task(card, "title");
        task.id = 1;
        stubFor(delete("/api/tasks/1").willReturn(
                aResponse().withHeader("Content-Type", "application/json").withBody(new ObjectMapper().writeValueAsString(task))
        ));
        ServerUtils server = new ServerUtils();
        assertDoesNotThrow(() -> server.deleteTask(task));
    }


}