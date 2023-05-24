package br.anhembi.jokenpogame.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker //permite o tratamento de mensagens WebSocket, apoiado por um intermediário de mensagens
public class WebSocketMessageConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //filtrar os destinos direcionados aos métodos anotados do aplicativo (via @MessageMapping )
        registry.setApplicationDestinationPrefixes("/app");

        //intermediário de mensagens na memória para transportar as mensagens de volta ao cliente em destinos prefixados
        registry.enableSimpleBroker("/topic");

    }
}
