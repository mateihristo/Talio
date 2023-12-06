package server.api;

import commons.Board;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import server.database.BoardRepository;




@Controller
public class WebsocketController {

    private final BoardRepository repo;

    private final BoardUpdateListener boardListener;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private BoardUpdateListener boardUpdateListener;

    public WebsocketController(BoardRepository repo){
        this.repo = repo;
        this.boardListener = new BoardUpdateListener();
    }

    @SubscribeMapping("/boardfeed/{boardId}")
    public Board sendBoardOnSubscription(@DestinationVariable("boardId") long boardId) throws Exception{
        if(!repo.existsById(boardId)){
            System.out.println("Couldn't be found");
            return null;

        }
        System.out.println("Found successfully");
        Board board = repo.findById(boardId).get();
        return board;
    }

    @Scheduled(fixedRate= 100, initialDelay = 2000)
    public void sendChangedBoard(){
        //System.out.println("Scheduled Check called");
        while(boardUpdateListener.hasNext()) {
            Board boardToSend = boardUpdateListener.poll();
            boardToSend = repo.findById(boardToSend.id).get();
            System.out.println(boardToSend);
            messagingTemplate.convertAndSend("/boards/boardfeed/" + boardToSend.id, boardToSend);
        }
    }
}
