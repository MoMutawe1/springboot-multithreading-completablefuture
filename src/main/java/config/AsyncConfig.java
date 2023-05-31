package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
// @EnableAsync: inform spring framework to run your task in background using thread pool concept.
// (to run @Async methods in a background thread pool)
@EnableAsync
public class AsyncConfig {

    // configure our thread pool by creating a bean of Executor.
    @Bean(name ="taskExecutor")
    public Executor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // initialize 2 threads (our thread capacity in our thread pool).
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100); // means at a time these many number of tasks can be wait in your blocking queue.
        executor.setThreadNamePrefix("userThread-");
        executor.initialize();
        return executor;
    }
}