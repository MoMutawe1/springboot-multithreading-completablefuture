package service;

import entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import repository.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepository userRepository;

    // @EnableAsync is used for enabling asynchronous processing with Java Spring Boot Configuration and switches Spring's ability to run @Async methods.
    // The @Async Methods run in the background thread pool without interruption other parallel processes.
    @Async
    public CompletableFuture<List<User>> findAllUsers(){
        log.info("get list of user by "+Thread.currentThread().getName());

        List<User> users=userRepository.findAll();
        return CompletableFuture.completedFuture(users);
    }

    // Read our CSV file then make it as an object to persist in Database.
    // The @Async Methods run in the background thread pool without interruption other parallel processes.
    @Async
    public CompletableFuture<List<User>> saveUsers(MultipartFile file) throws Exception {
        long start = System.currentTimeMillis();  // capture the current time in millis before start processing the file.

        List<User> users = parseCSVFile(file); // call the parseCSVFile to convert the file into a list of objects.
        log.info("saving list of users of size {}", users.size(), "" + Thread.currentThread().getName());
        users = userRepository.saveAll(users);

        long end = System.currentTimeMillis();  // capture the current time in millis after finish processing the file.
        log.info("Total time {}", (end - start));
        return CompletableFuture.completedFuture(users);
    }

    private List<User> parseCSVFile(final MultipartFile file) throws Exception {
        final List<User> users = new ArrayList<>();
        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] data = line.split(",");
                    final User user = new User();
                    user.setName(data[0]);
                    user.setEmail(data[1]);
                    user.setGender(data[2]);
                    users.add(user);
                }
                return users;
            }
        } catch (final IOException e) {
            log.error("Failed to parse CSV file {}", e);
            throw new Exception("Failed to parse CSV file {}", e);
        }
    }
}
