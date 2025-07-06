package com.engly.engly_server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {
    @Value("${app.async.task-executor.core-pool-size:20}")
    private int taskExecutorCorePoolSize;

    @Value("${app.async.task-executor.max-pool-size:100}")
    private int taskExecutorMaxPoolSize;

    @Value("${app.async.task-executor.queue-capacity:500}")
    private int taskExecutorQueueCapacity;

    @Value("${app.async.task-executor.keep-alive-seconds:60}")
    private int taskExecutorKeepAliveSeconds;

    @Value("${app.async.task-executor.thread-name-prefix:fast-async-}")
    private String taskExecutorThreadNamePrefix;

    @Value("${app.async.messageViewed-executor.core-pool-size:5}")
    private int messageViewedExecutorCorePoolSize;

    @Value("${app.async.messageViewed-executor.max-pool-size:25}")
    private int messageViewedExecutorMaxPoolSize;

    @Value("${app.async.messageViewed-executor.queue-capacity:1000}")
    private int messageViewedExecutorQueueCapacity;

    @Value("${app.async.message-executor.thread-name-prefix:messagesViewed-}")
    private String messageViewedExecutorThreadNamePrefix;

    @Bean("taskExecutor")
    @Primary
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskExecutorCorePoolSize);
        executor.setMaxPoolSize(taskExecutorMaxPoolSize);
        executor.setQueueCapacity(taskExecutorQueueCapacity);
        executor.setKeepAliveSeconds(taskExecutorKeepAliveSeconds);
        executor.setThreadNamePrefix(taskExecutorThreadNamePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        log.info("TaskExecutor configured: core={}, max={}, queue={}",
                taskExecutorCorePoolSize, taskExecutorMaxPoolSize, taskExecutorQueueCapacity);
        return executor;
    }

    @Bean("messageViewedExecutor")
    public TaskExecutor messageViewedExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(messageViewedExecutorCorePoolSize);
        executor.setMaxPoolSize(messageViewedExecutorMaxPoolSize);
        executor.setQueueCapacity(messageViewedExecutorQueueCapacity);
        executor.setThreadNamePrefix(messageViewedExecutorThreadNamePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();

        log.info("MessageViewedExecutor configured: core={}, max={}, queue={}",
                messageViewedExecutorCorePoolSize, messageViewedExecutorMaxPoolSize, messageViewedExecutorQueueCapacity);

        return executor;
    }

    @Bean("chatParticipantsExecutor")
    public TaskExecutor chatParticipantsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(300);
        executor.setKeepAliveSeconds(180);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();

        log.info("ChatParticipantsExecutor configured: core={}, max={}, queue={}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueSize());

        return executor;
    }
}
