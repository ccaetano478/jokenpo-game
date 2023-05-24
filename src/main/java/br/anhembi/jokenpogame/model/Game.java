package br.anhembi.jokenpogame.model;

import java.util.HashMap;
import java.util.Map;

public class Game {

    private String id;
    private Player player1;
    private Player player2;
    private MessageType status; //INIT, STARTED, FINISHED
    private String winner;
    private Player playerTurn;

    public Player getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(Player playerTurn) {
        this.playerTurn = playerTurn;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public MessageType getStatus() {
        return status;
    }

    public void setStatus(MessageType status) {
        this.status = status;
    }
}
