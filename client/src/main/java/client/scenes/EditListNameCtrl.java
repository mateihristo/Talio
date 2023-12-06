package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.inject.Inject;

public class EditListNameCtrl {
    private MainCtrl mainCtrl;
    private ServerUtils serverUtils;
    private Stage stage;
    // The ListContainerCtrl instance of the BoardList that is to be modified
    private ListContainerCtrl listContainerCtrl;
    @FXML
    private Label sceneTitle;
    @FXML
    private TextField listName;
    @FXML
    private Pane toolBar;
    @FXML
    private Button saveButton,minimizeButton, closeButton;
    private double x,y;

    @Inject
    public EditListNameCtrl(MainCtrl mainCtrl, ServerUtils serverUtils){
        this.mainCtrl=mainCtrl;
        this.serverUtils=serverUtils;
    }

    public void init(Stage stage, ListContainerCtrl listContainerCtrl){
        this.stage=stage;
        this.listContainerCtrl=listContainerCtrl;
        this.listName.setText(this.listContainerCtrl.getList().getName());

        toolBar.setOnMousePressed( mouseEvent -> {
            this.x= mouseEvent.getSceneX();
            this.y= mouseEvent.getSceneY();
        });
        toolBar.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX()-this.x);
            stage.setY(mouseEvent.getScreenY()-this.y);
        });
        saveButton.disableProperty().bind(listName.textProperty().isEmpty());

    }

    /**
     * Method which saves the new name entered by the User and propagates
     * the changes to the ListContainerCtrl instance
     */
    public void saveNewName(){
        this.listContainerCtrl.updateListName(this.listName.getText());
        close();
    }
    public void close(){
        this.stage.close();}
    public void clearName() {
        listName.clear();
    }
    public void minimize(){
        this.stage.setIconified(true);}


}
