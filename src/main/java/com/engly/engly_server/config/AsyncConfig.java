package com.engly.engly_server.config;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@EnableScheduling
@Slf4j
public class AsyncConfig implements SchedulingConfigurer, AsyncConfigurer {
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

    @Bean("taskScheduler")
    @Primary
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("app-scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        scheduler.initialize();

        log.info("Primary TaskScheduler configured: poolSize={}", scheduler.getPoolSize());
        return scheduler;
    }

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
        executor.setThreadNamePrefix("chat-participants-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();

        log.info("ChatParticipantsExecutor configured: core={}, max={}, queue={}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueSize());

        return executor;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
        log.info("Configured default scheduler for @Scheduled methods");
    }

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) ->
                log.error("Uncaught async exception in method {}: {}", method.getName(), ex.getMessage(), ex);
    }
}
