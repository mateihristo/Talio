package commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void getDescriptionTest() {
        Board t = new Board("", new ArrayList<>());
        BoardList r  = new BoardList("", new ArrayList<>(), t);
        Card testCard = new Card("cardTitle", "card description", r);
        assertEquals("card description", testCard.getDescription());
    }

    @Test
    void getTitleTest() {
        Board t = new Board("", new ArrayList<>());
        BoardList r  = new BoardList("", new ArrayList<>(), t);
        Card testCard = new Card("cardTitle", "card description", r);
        assertEquals("cardTitle", testCard.getTitle());
    }

    @Test
    void setDescriptionTest(){
        Board t = new Board("", new ArrayList<>());
        BoardList r  = new BoardList("", new ArrayList<>(), t);
        Card testCard = new Card("cardTitle", "card description", r);
        testCard.setDescription("new description");
        assertEquals("new description", testCard.getDescription());
    }

    @Test
    void setTitleTest(){
        Board t = new Board("", new ArrayList<>());
        BoardList r  = new BoardList("", new ArrayList<>(), t);
        Card testCard = new Card("cardTitle", "card description", r);
        testCard.setTitle("new title");
        assertEquals("new title", testCard.getTitle());
    }

    @Test
    void equalsTest(){
        Board t = new Board("", new ArrayList<>());
        BoardList r  = new BoardList("", new ArrayList<>(), t);
        Card testCard = new Card("cardTitle", "card description", r);
        Card otherCard = new Card("cardTitle", "card description", r);
        assertTrue(testCard.equals(otherCard));
        otherCard.setTitle("new title");
        assertFalse(testCard.equals(otherCard));
    }
    @Test
    void setParentListTest(){
        Board t = new Board("", new ArrayList<>());
        BoardList r  = new BoardList("", new ArrayList<>(), t);
        BoardList list  = new BoardList("", new ArrayList<>(), t);
        Card testCard = new Card("cardTitle", "card description", r);
        testCard.setParentList(list);
        assertEquals(list,testCard.getParentList());
    }
    @Test
    void setTaskListTest(){
        Board t = new Board("", new ArrayList<>());
        BoardList r  = new BoardList("", new ArrayList<>(), t);
        Card testCard = new Card("cardTitle", "card description", r);
        var lists=new ArrayList<Task>();
        lists.add(new Task(testCard,"a"));
        testCard.setTaskList(lists);
        assertEquals(lists,testCard.getTaskList());
    }
    @Test
    void addTaskTest(){
        Board t = new Board("", new ArrayList<>());
        BoardList r  = new BoardList("", new ArrayList<>(), t);
        Card testCard = new Card("cardTitle", "card description", r);
        var task=new Task(testCard,"a");
        testCard.addTask(task);
        assertEquals(testCard.getTaskList().get(0),task);
    }

    @Test
    void toStringTest(){
        String title = "cardName";
        String description = "cardDescription";
        String listName = "parentListName";
        List<Card> cardList = new ArrayList<>();
        BoardList parentList = new BoardList(listName, cardList);
        Card card = new Card(title, description, parentList);
        parentList.addCard(card);
        String expected = "Card{id=0, taskList=[], title='cardName', description='cardDescription'}";
        String result = card.toString();
        assertEquals(expected, result);
    }
}