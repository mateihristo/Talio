package server.api;


import commons.Board;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Hold on to your socks, because the backend is about to get more complicated:
 * An instance of BoardUpdateListener is autowired to every single controller of the server
 * Whenever an HTTP-request that makes a change to the Board
 * is handled by any of the REST-controllers, the associated board is added
 * to the updatedBoards Queue.
 *
 * Every 100ms the Queue is checked by the WebsocketController and the updated
 * Boards are sent to all clients that are subscribed to that certain Board
 *
 */
@Component
public class BoardUpdateListener {

    private Queue<Board> updatedBoards;

    public BoardUpdateListener(){
        
        this.updatedBoards = new ConcurrentLinkedQueue<>();
    }

    public synchronized void add(Board updatedBoard){
        this.updatedBoards.add(updatedBoard);
        
    }

    public Board poll(){
        return this.updatedBoards.poll();
    }

    public boolean hasNext(){
        return !this.updatedBoards.isEmpty();
    }
}
