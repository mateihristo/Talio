package commons;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class BoardList implements Serializable {

    /**
     * I named the list on the Board "BoardList" to prevent any confusion with java.util.List
     */

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;


    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "parentList", fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderColumn
    private List<Card> cardList;

    @JsonIgnore //this field needs to be ignored if converted to json, since it would otherwise be stuck in infinite loop
    @ManyToOne
    private Board parentBoard; // the parent Board of this BoardList
    private String name;


    public BoardList(){
        //default constructor that is necessary for Jackson to work, don't use !!
    }


    public BoardList(String name, List<Card> cardList, Board parentBoard){
        this.name = name;
        this.cardList = cardList;
        this.parentBoard = parentBoard;
    }

    public BoardList(String name, List<Card> cardList){
        this.name = name;
        this.cardList = cardList;
    }

    public List<Card> getCardList(){
        return this.cardList;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCardList(List<Card> cardList){
        this.cardList = cardList;
    }
    public void addCard(Card cardToAdd){
        this.cardList.add(cardToAdd);
    }

    public long getId() {
        return this.id;}

    public void setId(long id) {
        this.id = id;} //made this for testing, as the db isn't up and running yet.

    public void setParentBoard(Board parentBoard) {
        this.parentBoard = parentBoard;
    }
    public Board getParentBoard(){
        return this.parentBoard;
    }

    public void deleteCard(long id){
        //TODO
        //the id field of Card needs to be added in the future for database purposes
        //to be implemented here: deleting a card from cardList based on its id
        for(int i = 0; i < cardList.size(); i++){
            if(cardList.get(i).id == id){
                cardList.remove(i);
                break;
            }
        }
    }
    @Override
    public boolean equals(Object other){
        return other instanceof BoardList &&
               ((BoardList) other).cardList.equals(this.cardList) &&
               ((BoardList) other).name.equals(this.name) &&
               ((BoardList) other).parentBoard.equals(this.parentBoard);
    }


    @Override
    public String toString() {

        return "BoardList{" +
            "id=" + id +
            ", cardList=" + cardList +
            ", name='" + name + '\'' +
            '}';

    }
}
