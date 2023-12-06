package client.scenes;

import client.utils.ServerUtils;
import client.utils.WebsocketServerUtils;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import java.net.UnknownHostException;

/**
 * The controller for the ClientConnect.FXML
 */
public class ClientConnectCtrl {

    private final ServerUtils serverUtils;

    private final MainCtrl mainCtrl;

    private final WebsocketServerUtils websocketServerUtils;

    @FXML
    public AnchorPane toolBar;

    @FXML
    public Button closeButton, maximizeButton, minimizeButton;
    @FXML
    TextField serverAddressField;

    @FXML
    Button connectButton;

    /**
     * Constructor of the ClientConnectCtrl class
     * @param serverUtils to interact with the server through HTTP
     * @param mainCtrl the main controller
     */
    @Inject
    public ClientConnectCtrl(ServerUtils serverUtils, MainCtrl mainCtrl, WebsocketServerUtils websocketServerUtils) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.websocketServerUtils = websocketServerUtils;
    }

    /**
     * This method is called when the 'Connect' button is clicked.
     * (Or when enter is pressed, see the handleKeyPressed method below)
     * It retrieves the server address entered in the text field and tries to connect to the server.
     * (See connect() method in ServerUtils)
     * When connection is successful, the board overview is shown.
     * If the server address is invalid, the invalidServerAddressAlert pops up.
     */
    @FXML
    public void connectToServer() throws Exception {
        String serverAddress = serverAddressField.getText();

        try{
            serverUtils.connect(serverAddress);
            websocketServerUtils.setServer(serverAddress);
            mainCtrl.showBoardOverview();
        }catch(Exception exception){
            if(exception instanceof UnknownHostException){
                invalidServerAddressAlert();
            }else{
                exception.printStackTrace();
            }
        }
    }

    /**
     * This method is called when the user tries to connect, but entered an invalid server address.
     * We use an alert box with a message as a popup.
     * The user can then click OK and try again.
     */
    public void invalidServerAddressAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid server address of a Talio server", ButtonType.OK);
        alert.setHeaderText("Invalid server address");
        alert.showAndWait();
    }

    /**
     * This method is called when a key is pressed in the text field.
     * It checks if the pressed key is the Enter key and calls connectToServer() if it is.
     * Pressing enter is thus equivalent to clicking the connect button.
     * @param event The key event
     */
    @FXML
    public void handleKeyPressed(KeyEvent event) throws Exception {
        if (event.getCode() == KeyCode.ENTER) {
            connectToServer();
        }
    }

    /**
     * Function that is connected to the closeButton of the controller
     * It delegates the function of closing the app to the Main Controller
     */
    @FXML
    public void close(){
        this.mainCtrl.closeApp();}

    /**
     * Function that is connected to the minimizeButton of the controller
     * It delegates the function of minimizing the window of the app
     * to the Main Controller
     */
    @FXML
    public void minimize(){
        this.mainCtrl.minimizeStage();}

    /**
     * Method that is connected to the maximizeButton of the controller
     * It delegates the function of maximizing the window of the app
     */
    public void maxMin(){
        this.mainCtrl.maxMinStage();}

    //Following methods are for testing purposes only
    public ServerUtils getServerUtils(){
        return serverUtils;
    }

    public MainCtrl getMainCtrl(){
        return mainCtrl;
    }

    public WebsocketServerUtils getWebsocketServerUtils() {
        return websocketServerUtils;
    }

}
