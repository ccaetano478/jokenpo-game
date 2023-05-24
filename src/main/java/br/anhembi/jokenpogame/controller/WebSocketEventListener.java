package br.anhembi.jokenpogame.controller;

import br.anhembi.jokenpogame.model.Game;
import br.anhembi.jokenpogame.model.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class WebSocketEventListener {
    private static final Logger LOGGER = Logger.getLogger(WebSocketEventListener.class.getName());

    @Autowired
    private SimpMessageSendingOperations sendingOperations;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){


            LOGGER.log(Level.INFO, "Usuario Conectado");



    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        LOGGER.log(Level.INFO, "Usuario deslogado");
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        final String username = (String) headerAccessor.getSessionAttributes().get("username");

        final Game message = new Game();
//        message.setType(MessageType.DISCONNECT);
//        message.setFrom(username);

        sendingOperations.convertAndSend("/topic/public");
    }
}
