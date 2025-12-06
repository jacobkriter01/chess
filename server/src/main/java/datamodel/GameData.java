package datamodel;

import chess.*;

public class GameData {
    private int gameID;
    private String gameName;
    private String whiteUsername;
    private String blackUsername;
    private ChessGame game;
    private boolean gameOver;
    private String winner;

    public GameData(int gameID, String gameName, String whiteUsername, String blackUsername, ChessGame game) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.game = (game != null) ? game : new ChessGame();
        this.gameOver = false;
        this.winner = null;
    }

    public int getGameID(){
        return gameID;
    }
    public String getGameName(){
        return gameName;
    }
    public String getWhiteUsername(){
        return whiteUsername;
    }
    public String getBlackUsername(){
        return blackUsername;
    }
    public void setWhiteUsername(String whiteUsername){
        this.whiteUsername = whiteUsername;
    }
    public void setBlackUsername(String blackUsername){
        this.blackUsername = blackUsername;
    }
    public ChessGame getGame(){
        return game;
    }
    public void setGameOver(boolean gameOver){
        this.gameOver = gameOver;
    }
    public boolean isGameOver(){
        return gameOver;
    }
    public String getWinner(){
        return winner;
    }
    public void setWinner(String winner){
        this.winner = winner;
    }
}
