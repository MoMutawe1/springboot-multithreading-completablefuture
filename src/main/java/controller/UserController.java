package controller;

import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import service.UserService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping(value = "/users", produces = "application/json")
    public CompletableFuture<ResponseEntity> findAllUsers() {
        return  service.findAllUsers().thenApply(ResponseEntity::ok);
    }

    @PostMapping(value = "/users", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity saveUsers(@RequestParam(value = "files") MultipartFile[] files) throws Exception {
        for (MultipartFile file : files) {
            service.saveUsers(file);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // here we will split the task between multiple threads, then we will join them and return a response.
    // here we've divided our task into 3 peaces, but in our AsyncConfig file we have defined only 2 threads in our thread pool
    // so task 1 will be handled by thread 1, task 2 will be handled by thread 2, then thread 2 will handle task 3 once it's done with task 1.
    @GetMapping(value = "/getUsersByThread", produces = "application/json")
    public  ResponseEntity getUsers(){
        // the same api (findAllUsers) I will call by 3 different threads.
        CompletableFuture<List<User>> task1=service.findAllUsers();
        CompletableFuture<List<User>> task2=service.findAllUsers();
        CompletableFuture<List<User>> task3=service.findAllUsers();
        CompletableFuture.allOf(task1,task2,task3).join();
        return  ResponseEntity.status(HttpStatus.OK).build();
    }
}
