package br.anhembi.jokenpogame.controller;

import br.anhembi.jokenpogame.model.Game;
import br.anhembi.jokenpogame.model.GameLobbyMonitor;
import br.anhembi.jokenpogame.model.MessageType;
import br.anhembi.jokenpogame.model.Player;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MoveController {

    private  final SimpMessagingTemplate simpMessagingTemplate;

    public MoveController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/move")
    @SendTo("topic/game-progress/")

    public Game move (@Payload Player player)  {
        Game game = GameLobbyMonitor.getInstance().getGames().get(player.getGameId());
        game.setPlayerTurn(player);
        game.setStatus(MessageType.MOVE);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getId(), game);
        return game;
    }
}
