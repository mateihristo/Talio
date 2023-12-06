/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


import commons.Board;
import commons.BoardList;
import commons.Card;


import commons.Task;
import jakarta.ws.rs.core.Response;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;


/**
 * The connection between the GUI and the server
 */
public class ServerUtils {

    private static String SERVER = "http://localhost:8080/";

    private final static ExecutorService EXEC = Executors.newSingleThreadExecutor();
    public Card getCard(long cardId){
        return ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("api/cards/" + cardId)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .get(new GenericType<Card>(){});
    }

    public Card postNewCard(Card newCard, BoardList parentBoardList){
        Card card = ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("api/cards/new-card/" + parentBoardList.id)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.entity(newCard, APPLICATION_JSON), Card.class);
        card.setParentList(parentBoardList);
        return card;
    }



    public Card updateCardDescription(Card newCard){
        Card card = ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("api/cards/change-description/" + newCard.id +"/" + newCard.getDescription())
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .put(Entity.entity(newCard, APPLICATION_JSON), Card.class);
        return card;
    }

    /**
     * Deletes a card from the server
     * @param cardToDelete the card to be deleted
     */
    public void deleteCard(Card cardToDelete){
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/cards/delete/" + cardToDelete.id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }
    
    public BoardList getList(long listId){
        return ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("api/boardlists/" + listId)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .get(new GenericType<BoardList>(){});
    }


    public BoardList postNewList(BoardList newBoardList, Board parent){
        BoardList createdList = ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("api/boardlists/new-boardlist/" + parent.id)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.entity(newBoardList, APPLICATION_JSON), BoardList.class);
        createdList.setParentBoard(parent);
        return createdList;
    }

    public BoardList renameList(BoardList changedList, Board parent){
        BoardList list = ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("api/boardlists/" + changedList.id + "/" + changedList.getName())
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .put(Entity.entity(changedList, APPLICATION_JSON), BoardList.class);
        list.setParentBoard(parent);
        return list;
    }

    public void deleteList(BoardList listToDelete){
        ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("api/boardlists/" + listToDelete.id)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
                .delete();
    }
    public void deleteTask(Task taskToDelete){
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/delete/" + taskToDelete.id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }
    
    public Board getBoard(long boardId){
        Board board = ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("api/boards/" + boardId)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .get(new GenericType<Board>(){});
        for(BoardList i : board.getLists()){
            i.setParentBoard(board);
            for(Card j : i.getCardList()){
                j.setParentList(i);
                for(Task k : j.getTaskList()){
                    k.setParentCard(j);
                }
            }
        }
        return board;
    }


    public Board postNewBoard(Board newBoard){
        return ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("api/boards/new-board")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.entity(newBoard, APPLICATION_JSON), Board.class);
    }
    
    public Board renameBoard(Board changedBoard){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + changedBoard.id + "/" + changedBoard.getName())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(changedBoard, APPLICATION_JSON), Board.class);
    }
    
    public Card renameCard(Card changedCard){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/cards/" + changedCard.id + "/" + changedCard.getTitle())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(changedCard, APPLICATION_JSON), Card.class);
    }

    /**
     * Returns all the child BoardList objects of a Board instance
     * @param board - the parent Board instance
     * @return - the list of all BoardList objects
     */
    public List<BoardList> getLists(Board board){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/boardlists/get-all/"+board.id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<BoardList>>(){});
    }
    public Task postNewTask(Task newTask, Card parentCard){
        Task task = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/new-task/" + parentCard.id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(newTask, APPLICATION_JSON), Task.class);
        task.setParentCard(parentCard);
        return task;
    }

    public Task getTask(long listId){
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/" + listId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Task>(){});
    }

    /**
     * This method lets a user connect to the server with a server address.
     * The user can access the localhost.
     * The server address the user has to enter is localhost:8080
     * @param serverAddress The server address of the server to connect to
     * @throws Exception exception thrown when connection can't be established
     */
    public void connect(String serverAddress) throws Exception{
        SERVER = "http://" + serverAddress + "/";
        try{
            ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/connection-available")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<String>() {});
        } catch (Exception exception){
            connectionFailure();
            throw new Exception("Could not connect to server: " + exception);

        }
    }

    /**
     * This method is called when the user tries to connect, but connection fails.
     * We use an alert box with a message as a popup.
     * The user can then click OK and try again.
     */
    public void connectionFailure(){
        Alert alert = new Alert(Alert.AlertType.ERROR, "Could not connect to server", ButtonType.OK);
        alert.setHeaderText("Something went wrong");
        alert.showAndWait();
    }


    public void dropCardOnOtherList(Card movedCard, long newParentId, long newPos){
        ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("/api/boardlists/exchange-card/" + movedCard.getParentList().id + "/" + newParentId + "/" + movedCard.id + "/" + newPos)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .put(Entity.entity("", APPLICATION_JSON));
    }

    public Board getBoardOrCreateNew(){
        return ClientBuilder.newClient(new ClientConfig())
            .target(SERVER).path("/api/boards/get-stored-board-or-create-new")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .get(new GenericType<Board>(){});
    }


    public void pollBoardTitle(long id, Consumer<String> stringConsumer){
        EXEC.submit(() ->{
            while(!Thread.interrupted()){
                Response response = ClientBuilder.newClient(new ClientConfig())
                    .target(SERVER).path("/api/boards/poll-boardTitle/" + id)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get(Response.class);
                if(response.getStatus() == 204)continue;
                String result = response.readEntity(String.class);
                stringConsumer.accept(result);
        }});
    }

    public void stop(){
        EXEC.shutdownNow();
    }
}
