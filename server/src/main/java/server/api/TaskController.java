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
import server.database.TaskRepository;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskRepository repo;

    private final CardRepository parentRepo;

    private final BoardListRepository listRepo;

    private final BoardRepository boardRepo;

    @Autowired
    private BoardUpdateListener boardUpdateListener;

    public TaskController(TaskRepository repo, CardRepository parentRepo, BoardListRepository listRepo, BoardRepository boardRepo) {
        this.repo = repo;
        this.parentRepo = parentRepo;
        this.listRepo = listRepo;
        this.boardRepo = boardRepo;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable("id") long id){
        if(id < 0 || !repo.existsById(id)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @PostMapping("/new-task/{taskId}")
    public ResponseEntity<Task> getNewTask(@PathVariable("taskId") long taskId, @RequestBody Task newTask){
        if(newTask == null || newTask.getName() == null || !parentRepo.existsById(taskId)){
            return ResponseEntity.badRequest().build();
        }

        Card parentCard=parentRepo.getById(taskId);
        BoardList parentList=listRepo.getById(parentCard.getParentList().id);
        Board parentBoard=boardRepo.getById(parentList.getParentBoard().id);
        newTask.setParentCard(parentCard);
        parentCard.addTask(newTask);
        int index=0;
        for(int i=0;i<parentList.getCardList().size();i++){
            if(parentList.getCardList().get(i).id== parentCard.id){
                index=i;
                parentList.getCardList().set(index,parentCard);
                break;
            }
        }

        int index2=0;
        for(int i=0;i<parentBoard.getLists().size();i++){
            if(parentBoard.getLists().get(i).id == parentList.id){
                index2=i;
                parentBoard.getLists().set(index2,parentList);
                break;
            }
        }

        Board updatedBoard=boardRepo.saveAndFlush(parentBoard);
        boardUpdateListener.add(updatedBoard);
        int taskNumber=updatedBoard.getLists().get(index2).getCardList().get(index).getTaskList().size()-1;
        Task addedTask=updatedBoard.getLists().get(index2).getCardList().get(index).getTaskList().get(taskNumber);

        return ResponseEntity.ok(addedTask);
    }

    @PutMapping("/change-name/{taskId}/{newName}")
    public ResponseEntity<Task> changeName(@PathVariable("taskId") long taskId, @PathVariable("newName")String newName){
        if(!repo.existsById(taskId)) {
            return ResponseEntity.badRequest().build();
        }
        Task currentTask=repo.findById(taskId).get();
        currentTask.setName(newName);
        Task updatedTask=repo.save(currentTask);
        boardUpdateListener.add(updatedTask.getParentCard().getParentList().getParentBoard());
        return ResponseEntity.ok(updatedTask);
    }
    @DeleteMapping("delete/{id}")
    public void deleteTask(@PathVariable("id") long id) {
        try {
            Task taskToDelete=repo.getById(id);
            Card parentCard=parentRepo.getById(taskToDelete.getParentCard().id);
            parentCard.deleteTask(taskToDelete.id);
            repo.deleteById(id);
            Card changedCard=parentRepo.saveAndFlush(parentCard);
            boardUpdateListener.add(boardRepo.getById(changedCard.getParentList().getParentBoard().id));
        }catch(IllegalArgumentException e){
            System.out.println("The id for deleteTask is invalid");
            e.printStackTrace();
        }
    }
}