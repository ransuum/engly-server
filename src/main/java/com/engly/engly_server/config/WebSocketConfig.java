package com.engly.engly_server.config;

import com.engly.engly_server.exception.handler.CustomExceptionWebSocketHandlerDecorator;
import com.engly.engly_server.security.websocket.AuthChannelInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptor authChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic")
                .setHeartbeatValue(new long[]{10000, 10000})
                .setTaskScheduler(heartbeatTaskScheduler());
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("https://engly-client-blmg.vercel.app", "http://localhost:3000");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor, new SecurityContextChannelInterceptor());
        registration.taskExecutor()
                .corePoolSize(8)
                .maxPoolSize(16)
                .queueCapacity(200);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(8)
                .maxPoolSize(16)
                .queueCapacity(200);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(CustomExceptionWebSocketHandlerDecorator::new);
        registration.setMessageSizeLimit(128 * 1024); // Increased for richer messages
        registration.setSendBufferSizeLimit(1024 * 1024); // Increased buffer
        registration.setSendTimeLimit(30000); // Longer timeout
    }

    @Bean("heartbeatTaskScheduler")
    ThreadPoolTaskScheduler heartbeatTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(4);
        taskScheduler.setThreadNamePrefix("ws-heartbeat-");
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setAwaitTerminationSeconds(10);
        taskScheduler.initialize();

        log.info("WebSocket heartbeat TaskScheduler configured");
        return taskScheduler;
    }
}
