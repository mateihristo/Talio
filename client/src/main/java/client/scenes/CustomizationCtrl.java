package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.inject.Inject;


public class CustomizationCtrl {
    private MainCtrl mainCtrl;
    private ServerUtils serverUtils;
    private Stage stage;
    private CustomizationCtrl customizationCtrl;
    private BoardOverviewCtrl boardOverviewCtrl;
    private ListContainerCtrl listContainerCtrl;
    @FXML
    private Pane toolBar;
    private double x,y;
    @FXML
    private Button closeButton, minimizeButton, boardreset, listreset, addbutton;
    @FXML
    private ColorPicker boardbackgroundcp, boardfontcp, listbackgroundcp, listfontcp, presetbackgroundcp, presetfontcp;
    @FXML
    private VBox taskpresets;
    @FXML
    private TextField text;

    private Color defaultbackground;
    private Color defaultfont;
    
    @Inject
    public CustomizationCtrl(MainCtrl mainCtrl, ServerUtils serverUtils){
        this.mainCtrl=mainCtrl;
        this.serverUtils=serverUtils;
    }

    public void init(Stage stage, BoardOverviewCtrl boardOverviewCtrl){
        this.stage=stage;
        this.boardOverviewCtrl = boardOverviewCtrl;
        boardfontcp.setValue(Color.BLACK);
        listfontcp.setValue(Color.BLACK);
        
        toolBar.setOnMousePressed( mouseEvent -> {
            this.x= mouseEvent.getSceneX();
            this.y= mouseEvent.getSceneY();
        });
        toolBar.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX()-this.x);
            stage.setY(mouseEvent.getScreenY()-this.y);
        });
    }
    //task presets
    public void init(VBox vBox){
        text = new TextField("Color Preset");
        text.setMinHeight(25.0);
        text.setPrefHeight(25.0);
        text.setPrefWidth(175.0);
        
        Button removeButton = new Button("x");
        removeButton.setMinHeight(25.0);
        removeButton.setMaxHeight(25.0);
        removeButton.setPrefHeight(25.0);
        removeButton.setPrefWidth(25.0);

        Button defaultbutton = new Button("Set as default");
        defaultbutton.setMinHeight(25.0);
        defaultbutton.setMaxHeight(25.0);
        defaultbutton.setPrefHeight(100.0);
        defaultbutton.setPrefWidth(100.0);

        presetbackgroundcp = new ColorPicker(Color.WHITE);
        presetbackgroundcp.setPrefWidth(85);

        presetfontcp = new ColorPicker(Color.BLACK);
        presetfontcp.setPrefWidth(85);

        HBox presetHbox = new HBox(text, presetbackgroundcp, presetfontcp, removeButton, defaultbutton);
        presetHbox.setMinHeight(25.0);
        presetHbox.setMaxHeight(25.0);
        presetHbox.setPrefHeight(25.0);
        presetHbox.setPrefWidth(462.0);
        removeButton.setOnAction(event ->{
            taskpresets.getChildren().remove(presetHbox);
        });
        defaultbutton.setOnAction(event ->{
            setpreset();
        });

        taskpresets.getChildren().addAll(presetHbox);
        HBox button = new HBox(addbutton);
        taskpresets.getChildren().add(button);
    }

    public void close(){
        this.stage.close();
    }

    public void minimize(){
        this.stage.setIconified(true);
    }

    public void boardreset(){
        //reseting the background and colorpicker
        BackgroundFill backgroundFill = new BackgroundFill(null, null, null);
        Background background = new Background(backgroundFill);
        boardOverviewCtrl.gettilepane().setBackground(background);
        boardbackgroundcp.setValue(Color.WHITE);

        //reseting the font and colorpicker
        boardOverviewCtrl.getboardTitle().setStyle(null);
        boardfontcp.setValue(Color.BLACK);
    }

    public void boardchangebackground(){
        BackgroundFill backgroundFill = new BackgroundFill(boardbackgroundcp.getValue(), null, null);
        Background background = new Background(backgroundFill);
        boardOverviewCtrl.gettilepane().setBackground(background);
    }

    public void boardchangefont(){
        Color value = boardfontcp.getValue();
        String style = String.format("-fx-text-fill: %s;", toHexString(value));
        boardOverviewCtrl.getboardTitle().setStyle(style);
    }

    private static String toHexString(Color color) {
        int r = ((int) Math.round(color.getRed()     * 255)) << 24;
        int g = ((int) Math.round(color.getGreen()   * 255)) << 16;
        int b = ((int) Math.round(color.getBlue()    * 255)) << 8;
        int a = ((int) Math.round(color.getOpacity() * 255));
    
        return String.format("#%08X", (r + g + b + a));
    }

    public void listreset(){
        boardOverviewCtrl.setlistcolor(Color.WHITE);
        listbackgroundcp.setValue(Color.WHITE);
        boardOverviewCtrl.setlistnamecolor(Color.BLACK);
        listfontcp.setValue(Color.BLACK);
    }

    public void listchangebackground(){
        boardOverviewCtrl.setlistcolor(listbackgroundcp.getValue());
    }

    public void listchangefont(){
        boardOverviewCtrl.setlistnamecolor(listfontcp.getValue());
    }

    public void addpreset(){
        init(taskpresets);
    }

    public void setpreset(){
        boardOverviewCtrl.setpresetbackground(presetbackgroundcp.getValue());
        boardOverviewCtrl.setpresetfont(presetfontcp.getValue());
    }

    
}
