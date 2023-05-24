package br.anhembi.jokenpogame.model;

import java.util.HashMap;
import java.util.Map;

public class GameLobbyMonitor {

    private static Map<String, Game> games;
    private static GameLobbyMonitor instance;

    private GameLobbyMonitor() {
        games = new HashMap<>();
    }

    public static synchronized GameLobbyMonitor getInstance() {
        if (instance == null) {
            instance = new GameLobbyMonitor();
        }
        return instance;
    }

    public Map<String, Game> getGames() {
        return games;
    }

    public void setGame(Game game) {
        games.put(game.getId(), game);
    }

}
