package server.api;



import commons.Board;
import commons.BoardList;
import commons.Card;
import commons.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.BoardListRepository;
import server.database.BoardRepository;
import server.database.CardRepository;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    /**
     * repo - the JpaRepository that is used for all items of type Card
     */
    private final CardRepository repo;

    /**
     * parentRepo - the JpaRepository that is used to retrieve the parent BoardList of the card, in order to set the parentList field
     */
    private final BoardListRepository parentRepo;

    private final BoardRepository boardRepo;
    @Autowired
    private BoardUpdateListener boardUpdateListener;

    public CardController(CardRepository repo, BoardListRepository parentRepo, BoardRepository boardRepo){
        this.repo = repo;
        this.parentRepo = parentRepo;
        this.boardRepo = boardRepo;
    }


    /**
     * Gets a card by id
     * @param id - the id specified in the url, id specifies the id of the card to be retrieved
     * @return a ResponseEntity containing the card that has been requested by id
     */

    @GetMapping("/{id}")
    public ResponseEntity<Card> getById(@PathVariable("id") long id){
        if(id < 0 || !repo.existsById(id)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * Creates and adds a new card to a boardlist, the card is also added to the database
     * @param boardListId - the id of the boardlist to which the card should be added to, the boardlist should already exist in the jparepository
     * @return a ResponseEntity that contains the newly created card
     */

    @PostMapping("/new-card/{boardListId}")
    public ResponseEntity<Card> getNewCard(@RequestBody Card newCard, @PathVariable("boardListId") long boardListId){
        if(!parentRepo.existsById(boardListId)){
            return ResponseEntity.badRequest().build();
        }
        BoardList parentList = parentRepo.getById(boardListId);
        Board parentBoard = boardRepo.getById(parentList.getParentBoard().id);
        newCard.setParentList(parentList);
        parentList.addCard(newCard);
        int index = 0;
        for(int i = 0; i < parentBoard.getLists().size(); i++){
            if(parentBoard.getLists().get(i).id == parentList.id){
                parentBoard.getLists().set(i, parentList);
                index = i;
                break;
            }
        }
        Board updatedBoard = boardRepo.saveAndFlush(parentBoard);
        //Card saved = repo.save(newCard);
        boardUpdateListener.add(updatedBoard);
        Card addedCard = updatedBoard.getLists().get(index).getCardList().get(updatedBoard.getLists().get(index).getCardList().size() - 1);
        return ResponseEntity.ok(addedCard);
    }
    
    
  
    /**
     * Deletes the card from the database
     * @param id the ID of the card to delete
     * @throws IllegalArgumentException if the provided ID is null
     */
    @DeleteMapping("delete/{id}")
    public void deleteCard(@PathVariable("id") long id) {
        try {
            Card cardToDelete = repo.getById(id);
            BoardList parent = parentRepo.getById(cardToDelete.getParentList().id);
            parent.deleteCard(cardToDelete.id);
            repo.deleteById(id);
            BoardList affectedList = parentRepo.saveAndFlush(parent);
            boardUpdateListener.add(boardRepo.getById(affectedList.getParentBoard().id));
        }catch(IllegalArgumentException e){
            System.out.println("The id for deleteCard is invalid");
            e.printStackTrace();
        }
    }
    /**
     * Method that moves a task from one position (taskToMove) to a different one (newPos).
     * The method first removes the task and then add it to the correct position, so if we have card with 3 tasks(task0, task1, task2), to put task0 at the back
     * we would do a call with taskToMove=0 ad newPos=2.
     * @return a ResponseEntity that contains the modified card or a badrequests error if the method fails.
     */
    @PutMapping("/move-task/{id}/{taskToMove}/{newPos}")
    public ResponseEntity<Card> reorderTasks(@PathVariable("id") long id, @PathVariable("taskToMove") long taskToMove, @PathVariable("newPos") long newPos){
        if(!repo.existsById(id) || taskToMove <0 || newPos<0) {
            return ResponseEntity.badRequest().build();

        }
        Card card=repo.findById(id).get();
        var lists=card.getTaskList();
        if(taskToMove >=lists.size() || newPos>=lists.size()) {
            return ResponseEntity.badRequest().build();
        }
        Task movedTask=lists.get((int) taskToMove);
        lists.remove(movedTask);
        lists.add((int) newPos,movedTask);
        card.setTaskList(lists);
        Card updatedCard = repo.save(card);
        boardUpdateListener.add(updatedCard.getParentList().getParentBoard());
        return ResponseEntity.ok(updatedCard);
    }

    @PutMapping("/{id}/{newName}")
    public ResponseEntity<Card> renameCard(@PathVariable("id") long id, @PathVariable("newName") String newName) {
        if(!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Card currentCard = repo.findById(id).get();
        currentCard.setTitle(newName);
        Card updatedCard = repo.save(currentCard);
        boardUpdateListener.add(updatedCard.getParentList().getParentBoard());
        return ResponseEntity.ok(updatedCard);
    }
    @PutMapping("/change-description/{id}/{newDescription}")
    public ResponseEntity<Card> changeCardDescription(@PathVariable("id") long id, @PathVariable("newDescription") String newDescription) {
        if(!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Card currentCard = repo.findById(id).get();
        currentCard.setDescription(newDescription);
        Card updatedCard = repo.save(currentCard);
        boardUpdateListener.add(updatedCard.getParentList().getParentBoard());
        return ResponseEntity.ok(updatedCard);
    }
    
}
