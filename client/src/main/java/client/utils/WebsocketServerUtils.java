package client.utils;


import commons.Board;
import commons.BoardList;
import commons.Card;
import commons.Task;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.websocket.ClientEndpoint;
import java.lang.reflect.Type;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Utility class that manages the websocket connection with the backend
 */

@ClientEndpoint
public class WebsocketServerUtils {

    // ip-address of the server
    private String server = "ws://localhost:8080";

    // the board is constantly updated by the websocket, so this is where the most recent version of the board is stored
    private Board currentBoard = null;

    private WebSocketStompClient client;
    private StompSession session;
    private StompSession.Subscription subscription;

    /**
     * initialises the websocket connection to the server, please note that this does not mean that it will start
     * receiving Boards from the server, for that subscribeToBoard has to be called
     * @throws Exception - throws Exception if the connection can't be established in time
     */
    public void initSocket() throws Exception {
        this.client = new WebSocketStompClient(new StandardWebSocketClient());
        this.client.setMessageConverter(new MappingJackson2MessageConverter());

        this.session = this.client.connect(server + "/boardsession", new StompSessionHandlerAdapter() {}).get(10, SECONDS);
    }

    /**
     * subscribes to the board with the provided id and unsubscribes from any boards it is already connected to
     * @param boardId - id of the board to connect to
     */
    public void subscribeToBoard(long boardId)throws RuntimeException{
        if(this.session == null)throw new RuntimeException("The connection session is null: make sure initSocket is called");
        if(this.subscription != null)this.subscription.unsubscribe();
        this.subscription = this.session.subscribe("/boards/boardfeed/" + boardId, new GetBoardStompFrameHandler());
    }

    /**
     * getter for the most recent value of the board
     * @return - the most recent value of the board
     * @throws RuntimeException - if currentBoard is null a runtimeException is thrown
     */
    public Board getCurrentBoard() throws RuntimeException{
        if(this.currentBoard == null)throw new RuntimeException("The current board is null: make sure you are connected to the server");
        return this.currentBoard;
    }

    /**
     * setter for the value of the ip-address of the server
     * @param server - ip-address of the server
     */
    public void setServer(String server){
        this.server = "ws://" + server;
    }

    /**
     * getter for server, this is used for testing
     *@return string server
     */
    public String getServer(){
        return this.server;
    }

    /**
     * Handling class for whenever client receives a message from the server
     */
    private class GetBoardStompFrameHandler implements StompFrameHandler {

        
        @Override
        public Type getPayloadType(StompHeaders stompHeaders){
            return Board.class;
        }

        //This method constantly gets the board
        @Override
        public void handleFrame(StompHeaders stompHeaders, Object board){
            currentBoard = (Board) board;
            for(BoardList i : currentBoard.getLists()){
                i.setParentBoard(currentBoard);
                for(Card j : i.getCardList()){
                    j.setParentList(i);
                    for(Task k: j.getTaskList()){
                        k.setParentCard(j);
                    }
                }
            }
        }
    }
}
