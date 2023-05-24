package br.anhembi.jokenpogame.controller;

import br.anhembi.jokenpogame.model.Game;
import br.anhembi.jokenpogame.model.GameLobbyMonitor;
import br.anhembi.jokenpogame.model.MessageType;
import br.anhembi.jokenpogame.model.Player;
import br.anhembi.jokenpogame.web.State;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MoveController {

    private  final SimpMessagingTemplate simpMessagingTemplate;

    public static List<Player> playersThatMoved = new ArrayList<>();
    private State state = new State();

    public MoveController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;

    }

    @MessageMapping("/move")
    @SendTo("topic/game-progress/")

    public Game move (@Payload Player player)  {
        Game game = GameLobbyMonitor.getInstance().getGames().get(player.getGameId());
        game.setPlayerTurn(player);
        game.setStatus(MessageType.MOVE);

        if(!playersThatMoved.contains(player)){
            playersThatMoved.add(player);
        }
        if(playersThatMoved.size() == 2){
            state.gamePlay(playersThatMoved);
        }
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getId(), game);
        return game;
    }
}
