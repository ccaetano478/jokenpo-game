package br.anhembi.jokenpogame.controller;

import br.anhembi.jokenpogame.model.Game;
import br.anhembi.jokenpogame.model.GameLobbyMonitor;
import br.anhembi.jokenpogame.model.MessageType;
import br.anhembi.jokenpogame.model.Player;
import br.anhembi.jokenpogame.web.State;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {

    private State state;
    private static List<Player> players = new ArrayList<>();
    private  final SimpMessagingTemplate simpMessagingTemplate;

    private static Game game;
    @PostMapping("/start")
    public ResponseEntity<Game> start(@RequestBody Player player) {
        MoveController moveController = new MoveController(simpMessagingTemplate);
        log.info("Jogo iniciado");
        return ResponseEntity.ok(state.initGame(player));
    }

    @PostMapping("/join")
    public ResponseEntity<Game> connect(@RequestBody Player player)  {
        log.info("conectando a um jogo existente");
        Game game = state.joinGame(player);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getId(), game);
        return ResponseEntity.ok(game);
    }




    @PostMapping("/ws")
    public ResponseEntity<Game> gamePlay(@RequestBody List<Player> players)  {
        Game game = new Game();
        game.setStatus(MessageType.WAITING);

        if(players.size() == 2){
            log.info("gameplay");
            game = state.gamePlay(players);
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getId(), game);
            return ResponseEntity.ok(game);
        }
        return ResponseEntity.ok(game);
    }




/*
    @MessageMapping("chat.newUser")
    @SendTo("topic/public")

    public Message newUser (@Payload Message chatMessage, SimpMessageHeaderAccessor headerAccessor){
        headerAccessor.getSessionAttributes().put("username", chatMessage.getFrom());
        return chatMessage;
    }*/
}
