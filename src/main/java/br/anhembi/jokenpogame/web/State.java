package br.anhembi.jokenpogame.web;

import br.anhembi.jokenpogame.controller.MoveController;
import br.anhembi.jokenpogame.model.Game;
import br.anhembi.jokenpogame.model.GameLobbyMonitor;
import br.anhembi.jokenpogame.model.MessageType;
import br.anhembi.jokenpogame.model.Player;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Service
public class State {
    public Game initGame(Player player){
        Game game = new Game();
        game.setId(UUID.randomUUID().toString());
        game.setPlayer1(player);
        game.setStatus(MessageType.INIT);
        GameLobbyMonitor.getInstance().setGame(game);
        return game;
    }

    public Game joinGame(Player player2){
        Game game = GameLobbyMonitor.getInstance().getGames().values().stream().findAny().orElseThrow(()-> new RuntimeException("jogo não encontrado"));
        game.setPlayer2(player2);
        game.setStatus(MessageType.STARTED);
        GameLobbyMonitor.getInstance().setGame(game);
        new ModelAndView("redirect:/game/ws");
        return game;
    }

    public Game gamePlay(List<Player> players)  {
        if (!GameLobbyMonitor.getInstance().getGames().containsKey(players.get(0).getGameId())) {
            throw new RuntimeException("Game not found");
        }

        Game game = GameLobbyMonitor.getInstance().getGames().get(players.get(0).getGameId());
        if (game.getStatus().equals(MessageType.FINISHED)) {
            throw new RuntimeException("Game is already finished");
        }
        game.setPlayer1(players.get(0));
        game.setPlayer2(players.get(1));

        Player winner = checkWinner(game.getPlayer1(), game.getPlayer2());

        if (winner == null ) {
            game.setStatus(MessageType.FINISHED);
            game.setWinner("Empate");
            MoveController.playersThatMoved = null;
        } else {
            game.setStatus(MessageType.FINISHED);
            game.setWinner(winner.getLogin());
            MoveController.playersThatMoved = null;
        }

        GameLobbyMonitor.getInstance().setGame(game);
        return game;
    }

    private Player checkWinner (Player p1, Player p2){
        String inputP1 = p1.getInput();
        String inputP2 = p2.getInput();

        if (inputP1.equals("stone") && inputP2.equals("stone") ){
            return null;
        }
        if (inputP1.equals("stone") && inputP2.equals("scissors") ){
            return p1;
        }
        if (inputP1.equals("stone") && inputP2.equals("paper") ){
            return p2;
        }
        if (inputP1.equals("scissors") && inputP2.equals("scissors") ){
            return null;
        }
        if (inputP1.equals("scissors") && inputP2.equals("stone") ){
            return p2;
        }
        if (inputP1.equals("scissors") && inputP2.equals("papel") ){
            return p1;
        }
        if (inputP1.equals("paper") && inputP2.equals("paper") ){
            return null;
        }
        if (inputP1.equals("paper") && inputP2.equals("scissors") ){
            return p2;
        }
        if (inputP1.equals("paper") && inputP2.equals("stone") ){
            return p1;
        }
       return null;
    }
}
