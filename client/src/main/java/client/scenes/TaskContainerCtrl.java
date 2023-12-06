package client.scenes;

import client.utils.ServerUtils;
import commons.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;

import javax.inject.Inject;

public class TaskContainerCtrl extends VBox{
    private AddCardCtrl addCardCtrl;
    private CardOverviewCtrl cardOverviewCtrl;
    private CustomizationCtrl customizationCtrl;

    private MainCtrl mainCtrl;

    private final ServerUtils serverUtils;

    private TextField text;


    @Inject
    public TaskContainerCtrl(MainCtrl mainCtrl, ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
    }

    public void init(TilePane tilePane, AddCardCtrl addCardCtrl){
        tilePane.setAlignment(Pos.TOP_RIGHT);
        this.addCardCtrl=addCardCtrl;
        ///this.task=new Task(addCardCtrl.getCard(),"Empty"); ///i think this is useless since the tasks get added created when the save button is pressed
        //that is because the card doesnt exist before then
        text= new TextField("empty");
        text.setMinHeight(25.0);
        text.setPrefHeight(25.0);
        text.setPrefWidth(400.0);

        BackgroundFill backgroundFill = new BackgroundFill(addCardCtrl.getlistcontainer().getboardoverview().getpresetbackground(), null, null);
        Background background = new Background(backgroundFill);
        text.setBackground(background);

        Color value = addCardCtrl.getlistcontainer().getboardoverview().getpresetfont();
        String style = String.format("-fx-text-fill: %s;", toHexString(value));
        text.setStyle(style);

        Button removeButton = new Button("x");
        removeButton.setMinHeight(25.0);
        removeButton.setMaxHeight(25.0);
        removeButton.setPrefHeight(25.0);
        removeButton.setPrefWidth(25.0);
        removeButton.setStyle("-fx-background-radius: 1em;");

        HBox taskHbox=new HBox(text, removeButton);
        taskHbox.setMinHeight(25.0);
        taskHbox.setMaxHeight(25.0);
        taskHbox.setPrefHeight(25.0);
        taskHbox.setPrefWidth(442.0);
        removeButton.setOnAction(event ->{
            tilePane.getChildren().remove(TaskContainerCtrl.this);
        });

        getChildren().addAll(taskHbox);
    }

    public void init(TilePane tilePane, CardOverviewCtrl cardOverviewCtrl, Task task){
        tilePane.setAlignment(Pos.TOP_RIGHT);
        this.cardOverviewCtrl=cardOverviewCtrl;
        if(task==null) {
            text= new TextField("empty");
        }else{
            text = new TextField(task.getName());
        }
        //this.task=this.serverUtils.postNewTask(this.task,this.addCardCtrl.getCard());
        text.setMinHeight(25.0);
        text.setPrefHeight(25.0);
        text.setPrefWidth(365.0);

        BackgroundFill backgroundFill = new BackgroundFill(cardOverviewCtrl.getlistcontainer().getboardoverview().getpresetbackground(), null, null);
        Background background = new Background(backgroundFill);
        text.setBackground(background);

        Color value = cardOverviewCtrl.getlistcontainer().getboardoverview().getpresetfont();
        String style = String.format("-fx-text-fill: %s;", toHexString(value));
        text.setStyle(style);

        Button removeButton = new Button("x");
        removeButton.setMinHeight(25.0);
        removeButton.setMaxHeight(25.0);
        removeButton.setPrefHeight(25.0);
        removeButton.setPrefWidth(25.0);
        removeButton.setStyle("-fx-background-radius: 1em;");

        HBox taskHbox=new HBox(text, removeButton);
        taskHbox.setMinHeight(25.0);
        taskHbox.setMaxHeight(25.0);
        taskHbox.setPrefHeight(25.0);
        taskHbox.setPrefWidth(405.0);
        ///tilePane.setMargin(this, new Insets(10,0,0,0));
        removeButton.setOnAction(event ->{
            tilePane.getChildren().remove(TaskContainerCtrl.this);
        });

        getChildren().addAll(taskHbox);
    }

    public String getText(){
        return text.getText();
    }

    private static String toHexString(Color color) {
        int r = ((int) Math.round(color.getRed()     * 255)) << 24;
        int g = ((int) Math.round(color.getGreen()   * 255)) << 16;
        int b = ((int) Math.round(color.getBlue()    * 255)) << 8;
        int a = ((int) Math.round(color.getOpacity() * 255));
    
        return String.format("#%08X", (r + g + b + a));
    }
}
