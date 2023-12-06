package client.scenes;

import client.utils.ServerUtils;
import client.utils.WebsocketServerUtils;
import commons.Board;
import commons.BoardList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BoardOverviewCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;
    
    private final WebsocketServerUtils websocketServerUtils;
    private Board board;
    private double x,y;
    
    @FXML
    private Button closeButton,minimizeButton,maximizeButton,addList, renameBoardBtn, disconnectButton, customizationbutton, howToDrag;
    @FXML
    private Pane toolBar;
    @FXML
    private TilePane tilePane;
    @FXML
    private TextField boardTitle;

    private Color listcolor = Color.WHITE;
    private Color listnamecolor = Color.BLACK;

    private Color presetbackground = null;
    private Color presetfont = Color.BLACK;

    @Inject
    public BoardOverviewCtrl(MainCtrl mainCtrl, ServerUtils serverUtils, WebsocketServerUtils websocketServerUtils) {
        this.mainCtrl=mainCtrl;
        this.serverUtils=serverUtils;
        this.websocketServerUtils = websocketServerUtils;
    }
    /**
     * The function initializes the functionality of dragging the
     * window of the application
     * @param stage the primary stage of the application
     */
    public void init(Stage stage) throws Exception {
        
        //TODO Get the client to choose its board, so that you automatically get a board based on ID,
        // and you don't have to follow the procedure below
        
        /*
         * Note: this is the board, with its id I use, to test syncing
         */
        this.board = serverUtils.getBoardOrCreateNew();
        
        //You can delete this line in principle, but then the client sees "Title shortly" instead of the database title
        this.boardTitle.setText(this.board.getName());
    
        /*
         * ATTENTION: Steps to make sure that the syncing works on your local host (make sure 2 clients connect to the same board)
         * 1. Create a boardObject and propagate it to the database
         * 2. Look in the H2 console or via printing what your board id is
         * 3. When running a second client, comment out the postNewBoard function
         * 4. Use the serverUtils.getBoard method and enter the ID you got from the already existing board.
         */
    
        
        //This board is the one without id
        //Board board = new Board(this.boardTitle.getText(), new ArrayList<>());
        
        //Assign the board to the one postNewBoard creates (the one with generated id)
        //this.board = serverUtils.postNewBoard(board);
        
    
        toolBar.setOnMousePressed( mouseEvent -> {
            this.x= mouseEvent.getSceneX();
            this.y= mouseEvent.getSceneY();
        });
        toolBar.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX()-this.x);
            stage.setY(mouseEvent.getScreenY()-this.y);
        });
    
        //Initializes the socket
        websocketServerUtils.initSocket();
        
        //Makes sure that the client is subscribed to this board
        websocketServerUtils.subscribeToBoard(this.board.id);
    
        //Using AtomicBoolean, because it is more tread safe and to prevent data inconsistency when dealing with multiple threads
        AtomicBoolean userChangesField = new AtomicBoolean(false);
        
        // Set up a Timeline to update the GUI every 500 ms, this is better as the timer,
        // because this is mostly used for JavaFX applications, firstly I implemented it with AnimatedTimer,
        // but that took so much CPU resources
        
        Timeline timeline = new Timeline(
                //Call the refresh method twice every second
                new KeyFrame(Duration.millis(500), event -> {
                    
                    boolean hasChanged = refresh(userChangesField.get());
                    //Set the userChangesField to the value from the refresh() func
                    userChangesField.set(hasChanged);
                    
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        boardTitle.setOnMousePressed(event -> {
            userChangesField.set(true);

        });
    
        boardTitle.setOnKeyTyped(event -> {
            userChangesField.set(true);

        });

        serverUtils.pollBoardTitle(board.id, p ->{
            boardTitle.setText(p);
        });
        
    }

    /**
     * The function which is connected to the renameBtn
     * sets the board name to the new title
     * sets the label to the new title
     */
    public void renameBoardTitle(){
        this.board.setName(boardTitle.getText());
        this.board = serverUtils.renameBoard(this.board);
        boardTitle.setText(boardTitle.getText());
        
    }
    
    public TextField getboardTitle(){
        return boardTitle;
    }

    public Board getBoard() {
        return board;
    }

    public void setlistcolor(Color color){
        listcolor = color;
    }

    public Color getlistcolor(){
        return listcolor;
    }

    public Color getpresetbackground(){
        return presetbackground;
    }

    public void setpresetbackground(Color color){
        presetbackground = color;
    }

    public Color getpresetfont(){
        return presetfont;
    }

    public void setpresetfont(Color color){
        presetfont = color;
    }

    public void setlistnamecolor(Color color){
        listnamecolor = color;
    }

    public Color getlistnamecolor(){
        return listnamecolor;
    }

    public TilePane gettilepane(){
        return tilePane;
    }
    /**
     * Method which triggers the addition of an EMPTY List Container
     * Connected to the addList button
     */
    public void addNewList(){
        BoardList listToAdd = new BoardList("Empty list", new ArrayList<>(), this.board);
        //System.out.println("New list Button clicked");
        Platform.runLater(() -> serverUtils.postNewList(listToAdd, this.board));
        //this.addNewVbox(null);
    }

    /**
     * Method which triggers the addition of a List Container with the provided BoardList instance
     * @param boardList - the BoardList instance for the container
     */
    public void addList(BoardList boardList){
        this.addNewVbox(boardList);
    }

    /**
     * Method which creates a new ListContainer object
     * which contains a child BoardList instance of the Board
     */
    public void addNewVbox(BoardList boardList) {
        ListContainerCtrl listContainerCtrl = new ListContainerCtrl(this.mainCtrl,this.serverUtils);
        listContainerCtrl.init(tilePane,this,boardList);
        tilePane.getChildren().add((tilePane.getChildren().size() - 1), listContainerCtrl);
    }


    /**
     * Retrieves the list based on the order of the tilePane parent
     * right now it doesn't have any usage
     * @param num which list needs to be retrieved
     * @return the vBox with its elements
     */
    public VBox getChild(int num) {
        return (VBox) tilePane.getChildren().get(num);
    }

    /**
     * Refreshes the overview of the board with all the updates of the database
     *
     * @return the boolean if a client is editing the field
     */
    public boolean refresh(boolean isUserEditing){
        //TODO implements the logic related to retrieving the lists and displaying them
        this.board = websocketServerUtils.getCurrentBoard();
        //this.board = serverUtils.getBoard(this.board.id);
        List<BoardList> lists = this.board.getLists();

       
        //Use an ObservableList to directly display changes onto the TilePane
        ObservableList<Node> tilePaneChildren = tilePane.getChildren();
        int numChildren = tilePaneChildren.size();
        //Check if the size of bigger than one
        if (numChildren > 1) {
            //Delete all the children except the last one, so except the AddBtn
            tilePaneChildren.subList(0, numChildren - 1).clear();
        }
    
        
        for (BoardList list : lists) {
            if(list.getCardList().size() == 0) {
                // to make sure that at least 1 cell gets allocated to enable dropping on empty list
                list.getCardList().add(null);
            }
            addList(list);
            
            //System.out.println(list.getParentBoard());
        }
        
        renameBoardBtn.disableProperty().bind((boardTitle.textProperty().isEqualTo(this.board.getName())
                .or(boardTitle.textProperty().isEmpty())));
        
    
        //If the user is not editing the textField, then you can set the boardTitle textField to the new value
        //Otherwise the clients get constantly interrupted
        /*if (!isUserEditing) {
            boardTitle.setText(this.board.getName());

            
        }*/
        
        //Return false again after the label has been set
        return false;
    }
    
    /**
     * Function that is connected to the closeButton of the controller
     * It delegates the function of closing the app to the Main Controller
     */
    public void close(){
        serverUtils.stop();
        this.mainCtrl.closeApp();
        
    }
    
    /**
     * Function that is connected to the minimizeButton of the controller
     * It delegates the function of minimizing the window of the app
     * to the Main Controller
     */
    public void minimize(){
        this.mainCtrl.minimizeStage();}

    /**
     * Method that is connected to the maximizeButton of the controller
     * It delegates the function of maximizing the window of the app
     */
    public void maxMin(){
        this.mainCtrl.maxMinStage();}

    /**
     * This method is called when the disconnect button is clicked so a user can switch servers.
     * The WelcomeOverview is shown so the user can enter a different server address.
     */
    @FXML
    public void disconnect() {
        mainCtrl.showWelcomeOverview();
    }

    /**
     * This method is called when the customization button is clicked so a user can customize the board.
     */
    @FXML
    public void customization() {
        mainCtrl.showCustomization();
    }

    //Next methods are getters used in testing
    public ServerUtils getServerUtils() {
        return serverUtils;
    }

    public MainCtrl getMainCtrl() {
        return mainCtrl;
    }

    public WebsocketServerUtils getWebsocketServerUtils() {
        return websocketServerUtils;
    }

    @FXML
    public void howToDragInfo() {
        String contentText = "Drag a task by clicking on its title. Drop it on the first open spot of a list or on top of a card to place it above that card.";
        Alert alert = new Alert(Alert.AlertType.INFORMATION, contentText, ButtonType.OK);
        alert.setHeaderText("How to drag and drop tasks:");
        alert.showAndWait();
    }
}