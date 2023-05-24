package br.anhembi.jokenpogame.web;

import br.anhembi.jokenpogame.model.Game;
import br.anhembi.jokenpogame.model.GameLobbyMonitor;
import br.anhembi.jokenpogame.model.MessageType;
import br.anhembi.jokenpogame.model.Player;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.DispatcherServlet;
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

        Player winner = checkWinner(game.getPlayer1(), game.getPlayer2());

        if (winner == null ) {
            game.setWinner("Empate");
        } else {
            game.setWinner(winner.getLogin());
        }

        GameLobbyMonitor.getInstance().setGame(game);
        return game;
    }

    private Player checkWinner (Player p1, Player p2){
        String inputP1 = p1.getInput();
        String inputP2 = p2.getInput();

        if (inputP1.equals("pedra") && inputP2.equals("pedra") ){
            return null;
        }
        if (inputP1.equals("pedra") && inputP2.equals("tesoura") ){
            return p1;
        }
        if (inputP1.equals("pedra") && inputP2.equals("papel") ){
            return p2;
        }
        if (inputP1.equals("tesoura") && inputP2.equals("tesoura") ){
            return null;
        }
        if (inputP1.equals("tesoura") && inputP2.equals("pedra") ){
            return p2;
        }
        if (inputP1.equals("tesoura") && inputP2.equals("papel") ){
            return p1;
        }
        if (inputP1.equals("papel") && inputP2.equals("papel") ){
            return null;
        }
        if (inputP1.equals("papel") && inputP2.equals("tesoura") ){
            return p2;
        }
        if (inputP1.equals("papel") && inputP2.equals("pedra") ){
            return p1;
        }
       return null;
    }
}
