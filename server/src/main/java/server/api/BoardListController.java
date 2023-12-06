package server.api;



import commons.Board;
import commons.BoardList;

import commons.Card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.BoardListRepository;
import server.database.BoardRepository;
import server.database.CardRepository;
import server.database.TaskRepository;

import java.util.List;


@RestController
@RequestMapping("/api/boardlists")
public class BoardListController {

    /**
     * repo - the JpaRepository that is used for all items of type BoardList
     */
    private final BoardListRepository repo;

    @Autowired
    private BoardUpdateListener boardUpdateListener;

    /**
     * parentRepo - the JpaRepository that is used for all items of type Board. This repo is necessary
     * for retrieving the value of the parentBoard field when constructing a new BoardList
     */
    private final BoardRepository parentRepo;

    private final CardRepository cardRepo;

    private final TaskRepository taskRepo;

    public BoardListController(BoardListRepository repo, BoardRepository parentRepo,CardRepository cardRepo, TaskRepository taskRepo){
        this.repo = repo;
        this.parentRepo = parentRepo;
        this.cardRepo = cardRepo;
        this.taskRepo = taskRepo;
    }

    /**
     * Gets a BoardList by id
     * @param id - the id of the BoardList that has to be retrieved
     * @return a ResponseEntity containing the BoardList that was requested by id, or a badrequest error if id is invalid
     */
    @GetMapping("/{id}")
    ResponseEntity<BoardList> getById(@PathVariable("id") long id){
        if(id < 0 || !repo.existsById(id)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());

    }

    /**
     * Returns all the child BoardList objects of a particular Board instance
     * @param boardId - the id the parent board
     * @return a ResponseEntity containing the List of all child BoardList objects
     */
    @GetMapping("/get-all/{boardId}")
    public ResponseEntity<List<BoardList>> getLists(@PathVariable("boardId") long boardId){
        if(boardId < 0 || !this.parentRepo.existsById(boardId)){
            return ResponseEntity.badRequest().build();
        }
        Board parentBoard = this.parentRepo.getById(boardId);
        List<BoardList> availableLists = parentBoard.getLists();
        return ResponseEntity.ok(availableLists);
    }

    /**
     * Creates a new BoardList as a child of the board for which the id is passed
     * @param boardId - the id of the board to which the BoardList should be added
     * @return a ResponseEntity containing the newly created BoardList or a badrequest error if id is invalid
     */
    @PostMapping("/new-boardlist/{boardId}")
    ResponseEntity<BoardList> getNewList(@RequestBody BoardList newList,@PathVariable("boardId") long boardId){
        if(boardId < 0 || !parentRepo.existsById(boardId)){
            return ResponseEntity.badRequest().build();
        }
        Board parentBoard = parentRepo.getById(boardId);
        newList.setParentBoard(parentBoard);
        //BoardList addedList = repo.saveAndFlush(newList);
        parentBoard.addList(newList);
        //parentBoard.addList(addedList);
        //Board updatedBoard = parentRepo.saveAndFlush(parentBoard);
        Board updatedBoard = parentRepo.saveAndFlush(parentBoard);
        boardUpdateListener.add(updatedBoard);
        return ResponseEntity.ok(newList);
    }

    @PutMapping("/{id}/{newName}")
    public ResponseEntity<BoardList> renameList(@PathVariable("id") long id, @PathVariable("newName") String newName) {
        if(!repo.existsById(id))return ResponseEntity.badRequest().build();
        BoardList currentList = repo.findById(id).get();
        currentList.setName(newName);
        BoardList updatedList = repo.save(currentList);
        boardUpdateListener.add(updatedList.getParentBoard());
        return ResponseEntity.ok(updatedList);
    }

    /**
     * This method deletes a list, as well as all of the cards and tasks associated with it.
     */
    @DeleteMapping("/{id}")
    public void deleteList(@PathVariable("id") long id){
        //System.out.println("Deletion called");
        if(!repo.existsById(id)){
            System.out.println("Invalid id for deleting list");
            return;
        }
        BoardList listToDelete = repo.getById(id);
        Board affectedBoard = parentRepo.getById(listToDelete.getParentBoard().id);
        affectedBoard.deleteList(listToDelete.id);
        repo.deleteById(id);
        Board updatedBoard = parentRepo.saveAndFlush(affectedBoard);
        boardUpdateListener.add(updatedBoard);
    }

    /**
     * Method that moves a card from one position (cardToMove) to a different one (newPos).
     * The method first removes the card and then add it to the correct position, so if we have boardlist with 3 cards(card0, card1, card2), to put card0 at the back
     * we would do a call with cardToMove=0 ad newPos=2.
     * @return a ResponseEntity that contains the modified boardlist or a badrequests error if the method fails.
     */
    @PutMapping("/move-card/{id}/{cardToMove}/{newPos}")
    public ResponseEntity<BoardList> reorderCards(@PathVariable("id") long id, @PathVariable("cardToMove") long cardToMove, @PathVariable("newPos") long newPos){
        if(!repo.existsById(id) || cardToMove <0 || newPos<0) {
            return ResponseEntity.badRequest().build();

        }
        BoardList boardlist=repo.findById(id).get();
        var lists=boardlist.getCardList();
        if(cardToMove >=lists.size() || newPos>=lists.size()) {
            return ResponseEntity.badRequest().build();
        }
        Card movedCard=lists.get((int) cardToMove);
        lists.remove(movedCard);
        lists.add((int) newPos,movedCard);
        boardlist.setCardList(lists);
        BoardList updatedBoardList = repo.save(boardlist);
        boardUpdateListener.add(updatedBoardList.getParentBoard());
        return ResponseEntity.ok(updatedBoardList);
    }

    /**
     * Method that moves a card between two boards (fromListId and toListId). Specifically the card from position cardToMove from the first board is moved to
     * position newPos of the second board
     * @return  a ResponseEntity that contains the boardlist that received the new card or a badrequests error if the method fails.
     */
    @PutMapping("/exchange-card/{fromListId}/{toListId}/{cardToMove}/{newPos}")
    public ResponseEntity<BoardList> exchangeCard(@PathVariable("fromListId")long fromListId,@PathVariable("toListId")long toListId,@PathVariable("cardToMove")long cardToMoveId, @PathVariable("newPos")long newPos){
        if(!repo.existsById(fromListId) || !repo.existsById(toListId) || cardToMoveId<0 || newPos<0){
            return ResponseEntity.badRequest().build();
        }
        BoardList oldList = repo.findById(fromListId).get();
        BoardList newList = repo.findById(toListId).get();
        List<Card> listsNew = newList.getCardList();
        if(newPos>listsNew.size()){//newPos can be equal to the size of the list, as that adds the element at the end of the list
            return ResponseEntity.badRequest().build();
        }
        Card movedCard = cardRepo.findById(cardToMoveId).get();
        movedCard.setParentList(newList);
        if(fromListId == toListId){
            listsNew.remove(movedCard);
            listsNew.add((int)newPos, movedCard);
        }else{
            oldList.deleteCard(movedCard.id);
            listsNew.add((int)newPos, movedCard);
        }

        newList.setCardList(listsNew);
        repo.saveAndFlush(oldList);
        BoardList updatedNewList=repo.saveAndFlush(newList);
        boardUpdateListener.add(parentRepo.getById(updatedNewList.getParentBoard().id));
        return ResponseEntity.ok(updatedNewList);
    }



}
