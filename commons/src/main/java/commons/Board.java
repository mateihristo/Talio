package commons;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


/**
 * Class used for creating boards
 */
@Entity
public class Board implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;


    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "parentBoard",fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderColumn
    private List<BoardList> lists;
    private String name;
    
    public Board(){
        // default constructor that is necessary for Jackson to work properly don't use this!!
    }

    public Board(String name, List<BoardList> lists){
        this.name = name;
        this.lists = lists;
    }

    public List<BoardList> getLists(){
        return this.lists;
    }

    public void setLists(List<BoardList> lists){
        this.lists = lists;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    
    //TODO Use this, but need to compare cardlists also
    @Override
    public boolean equals(Object other){
        return other instanceof Board &&
                ((Board) other).lists.equals(this.lists) &&
                ((Board) other).name.equals(this.name);
       
    }
    
    public void addList(BoardList boardList){
        lists.add(boardList);
        boardList.setParentBoard(this);
    }

    public void deleteList(long id){
        /*for(BoardList list:lists){
            if(list.getId() == id){
                lists.remove(list);
                break;
            }
        }*/
        for(int i = 0; i < lists.size(); i++){
            if(lists.get(i).id == id){
                lists.remove(i);
                break;
            }
        }
    }

    @Override
    public String toString() {

        return "Board{" +
            "id=" + id +
            ", lists=" + lists +
            ", name='" + name + '\'' +
            '}';

    }
}
