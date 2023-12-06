package commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void getListsTest() {
        Board testBoard = new Board("testboard", new ArrayList<BoardList>());
        assertEquals(new ArrayList<BoardList>(), testBoard.getLists());
    }

    @Test
    void setLists() {
        Board testBoard = new Board("testboard", new ArrayList<BoardList>());
        List<BoardList> testList = new ArrayList<BoardList>();
        testList.add(new BoardList("", new ArrayList<>(), testBoard));
        testBoard.setLists(testList);
        assertEquals(new BoardList("", new ArrayList<>(), testBoard), testBoard.getLists().get(0));
    }

    @Test
    void getName() {
        Board testBoard = new Board("testboard", new ArrayList<BoardList>());
        assertEquals("testboard", testBoard.getName());
    }

    @Test
    void setName() {
        Board testBoard = new Board("testboard", new ArrayList<BoardList>());
        testBoard.setName("new name");
        assertEquals("new name", testBoard.getName());
    }


    @Test
    void testEquals() {
        Board testBoard = new Board("testboard", new ArrayList<BoardList>());
        Board otherBoard = new Board("testboard", new ArrayList<BoardList>());
        assertEquals(testBoard, otherBoard);
        otherBoard.setName("new name");
        assertNotEquals(testBoard, otherBoard);
    }
    @Test
    void addListTest(){
        Board testBoard = new Board("testboard", new ArrayList<BoardList>());
        BoardList testBoardList=new BoardList("test",new ArrayList<Card>());
        testBoard.addList(testBoardList);
        assertEquals(new BoardList("test",new ArrayList<Card>(),testBoard),testBoard.getLists().get(0));
    }
    @Test
    void deleteListTest(){
        Board testBoard = new Board("testboard", new ArrayList<BoardList>());
        BoardList testBoardList=new BoardList("test",new ArrayList<Card>());
        testBoardList.setId(56);
        testBoard.addList(testBoardList);
        testBoard.deleteList(56);
        assertEquals(new ArrayList<BoardList>(),testBoard.getLists());
    }

    @Test
    void toStringTest(){
        String boardName = "boardName";
        List<BoardList> lists = new ArrayList<>();
        String listOneName = "listOneName";
        String listTwoName = "listTwoName";
        List<Card> cards = new ArrayList<>();
        BoardList listOne = new BoardList(listOneName, cards);
        BoardList listTwo = new BoardList(listTwoName, cards);
        Board board = new Board(boardName, lists);
        board.addList(listOne);
        board.addList(listTwo);
        String expected = "Board{id=0, lists=[BoardList{id=0, cardList=[], name='listOneName'}, BoardList{id=0, cardList=[], name='listTwoName'}], name='boardName'}";
        String result = board.toString();
        assertEquals(expected, result);
    }
}