package client.scenes;

import client.utils.ServerUtils;
import commons.Card;

import commons.Task;

import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;



public class CardOverviewCtrl {
    private MainCtrl mainCtrl;
    private ServerUtils serverUtils;
    private Stage stage;
    private ListContainerCtrl listContainerCtrl;
    // The Card object that is displayed
    private Card card;
    private int cardIndex;
    @FXML
    private Button saveButton,removeButton, closeButton,minimizeButton;
    @FXML
    private Pane toolBar;
    @FXML
    private TextField title;
    @FXML
    private TextArea description;
    @FXML
    private TilePane tilePane;
    @FXML
    private Button newTaskButton;
    private double x,y;
    @Inject
    public CardOverviewCtrl(MainCtrl mainCtrl, ServerUtils serverUtils){
        this.mainCtrl=mainCtrl;
        this.serverUtils=serverUtils;
    }
    /**
     * The function initializes the functionality of dragging the
     * window of the application
     * @param stage the primary stage of the application
     */
    public void init(Stage stage, ListContainerCtrl listContainerCtrl,Card card){
        this.stage=stage;
        this.listContainerCtrl=listContainerCtrl;
        this.card=card;
        this.cardIndex=this.listContainerCtrl.getList().getCardList().indexOf(this.card);
        this.title.setText(this.card.getTitle());
        this.description.setText(this.card.getDescription());

        HBox hBoxOfNewTaskButton=new HBox(newTaskButton);
        hBoxOfNewTaskButton.setMinWidth(405.0);
        tilePane.getChildren().add(hBoxOfNewTaskButton);
        for(Task task:card.getTaskList()){
            if(task!=null) {
                addTask(task);
            }
        }
//        this.tasks.setText(this.card.getTasks().toString());
        toolBar.setOnMousePressed( mouseEvent -> {
            this.x= mouseEvent.getSceneX();
            this.y= mouseEvent.getSceneY();
        });
        toolBar.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX()-this.x);
            stage.setY(mouseEvent.getScreenY()-this.y);
        });
        saveButton.disableProperty().bind(title.textProperty().isEmpty());
    }

    public void saveChanges(){
        if(!this.title.getText().equals(this.card.getTitle())){
            this.card.setTitle(this.title.getText());
            Platform.runLater(() -> serverUtils.renameCard(this.card));
        }
        if(!this.description.getText().equals(this.card.getDescription())){
            Platform.runLater(() -> serverUtils.updateCardDescription(this.card));
            this.card.setDescription(this.description.getText());
        }

        var children=tilePane.getChildren();
        var tasksText=new ArrayList<String>();
        for(Node node:children){// we get the text from the taskscontainers that are visible in the GUI
            if(node.getClass()== TaskContainerCtrl.class) {
                TaskContainerCtrl container = (TaskContainerCtrl) node;
                tasksText.add(container.getText());
            }
        }
        //we compare the strings we got earlier with what the card originally had
        //if they didnt change, there's no need to do any db updates
        boolean tasksDidntChange=true;
        if(tasksText.size()!=card.getTaskList().size())
            tasksDidntChange=false;
        else {
            for (int i = 0; i < tasksText.size(); i++) {
                if (!tasksText.get(i).equals(card.getTaskList().get(i).getName())) {
                    tasksDidntChange = false;
                    break;
                }
            }
        }
        if(!tasksDidntChange){//if the tasks have changed, we delete the old ones and add the new ones to the db
            for(int i=0;i<card.getTaskList().size();i++){
                Task taskToDelete=card.getTaskList().get(i);
                if(taskToDelete!=null) {
                    Platform.runLater(() -> this.serverUtils.deleteTask(taskToDelete));
                }
            }
            card.getTaskList().clear();
            for (String text : tasksText) {
                Task taskToAdd = new Task(this.card, text);
                Platform.runLater(() -> this.serverUtils.postNewTask(taskToAdd, this.card));
            }
        }
        this.listContainerCtrl.updateCard(this.card,this.cardIndex);
        this.closeCard();
    }
    public void removeCard(){
        System.out.println("Remove card called");
        this.listContainerCtrl.removeCard(this.card);
        this.card.setParentList(null);
        closeCard();
    }
    /**
     * Method linked to the closeButton which switches the scene of the primary stage to the BoardOverview
     * after the changes to card were/were not saved
     */
    public void clearFields() {
        this.title.clear();
        this.description.clear();
        clearTasks();
    }
    public void clearTitle() {
        this.title.clear();
    }
    public void cancel() {
        clearFields();
    }
    public void clearDescription() {
        this.description.clear();
    }
    public void clearTasks(){
        ///loop thru taskContainers and delete the
        Iterator<Node> itr=tilePane.getChildren().iterator();
        while(itr.hasNext()){
            Node node= itr.next();
            itr.remove();
        }
    }
    public void closeCard(){
        cancel();
        ListContainerCtrl.setCardDialogOpen(false);
        this.stage.close();
    }
    public void minimize(){
        this.stage.setIconified(true);}

    public void addTask(){
        TaskContainerCtrl taskContainerCtrl=new TaskContainerCtrl(this.mainCtrl,this.serverUtils);
        taskContainerCtrl.init(tilePane,this,null);
        tilePane.getChildren().add(tilePane.getChildren().size()-1,taskContainerCtrl);
    }

    public void addTask(Task task){
        TaskContainerCtrl taskContainerCtrl=new TaskContainerCtrl(this.mainCtrl,this.serverUtils);
        taskContainerCtrl.init(tilePane,this,task);
        tilePane.getChildren().add(tilePane.getChildren().size()-1,taskContainerCtrl);
    }
    public Card getCard() {
        return this.card;
    }

    public ListContainerCtrl getlistcontainer(){
        return listContainerCtrl;
    }
}