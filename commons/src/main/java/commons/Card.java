package commons;



import com.fasterxml.jackson.annotation.JsonIgnore;


import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Card implements Serializable {
    /**
     * Cards contain a title and a description
     */

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "parentCard",fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderColumn
    private List<Task> taskList;

    @JsonIgnore //this field needs to be ignored if converted to json, since it would otherwise be stuck in infinite loop
    @ManyToOne
    private BoardList parentList; //the parent of this card
    private String title;
    private String description;

    public Card(){
        // default constructor that is necessary for Jackson, don't use!!
        // Jackson starts complaining if it is private access
    }
    public Card(String title, String description, BoardList parentList){
        this.title = title;
        this.description = description;
        this.parentList = parentList;
        this.taskList=new ArrayList<>();
    }
    public String getDescription() {
        return this.description;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setParentList(BoardList list){
        this.parentList = list;}

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
    public void addTask(Task task){
        this.taskList.add(task);
    }

    public BoardList getParentList(){
        return this.parentList;
    }
    @Override
    public boolean equals(Object other){
        return other instanceof Card &&
               ((Card)other).title.equals(this.title) &&
               ((Card)other).description.equals(this.description) &&
               ((Card)other).parentList.equals(this.parentList);
    }

    @Override
    public String toString() {
        return "Card{" +
            "id=" + id +
            ", taskList=" + taskList +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            '}';
    }

    /**
     * deletes task based on id
     */
    public void deleteTask(long id){
        for(int i=0;i<taskList.size();i++){
            if(taskList.get(i).id==id){
                taskList.remove(i);
                break;
            }
        }
    }
}
