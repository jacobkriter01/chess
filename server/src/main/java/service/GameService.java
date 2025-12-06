package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import datamodel.AuthTokenData;
import datamodel.GameData;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.ServiceException;
import exceptions.UnauthorizedException;

public class GameService {
    private final MySqlDataAccess dataAccess;

    public GameService(MySqlDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String token, String gameName) throws ServiceException{
        AuthTokenData auth = dataAccess.getAuthToken(token);
        if(auth == null){
            throw new UnauthorizedException();
        }
        if (gameName == null || gameName.isEmpty()){
            throw new BadRequestException();
        }
        return dataAccess.addGame(gameName);
    }

    public void joinGame(String token, int gameID, String color) throws ServiceException {
        AuthTokenData auth = dataAccess.getAuthToken(token);
        if (auth == null){
            throw new UnauthorizedException();
        }

        GameData game = dataAccess.getGame(gameID);
        if (game == null){
            throw new BadRequestException();
        }

        String username = auth.username();
        if(color == null || color.isEmpty()) {
            throw new BadRequestException();
        }
        if (color.equalsIgnoreCase("WHITE")){
            if (game.getWhiteUsername() != null && !game.getWhiteUsername().isEmpty()){
                throw new AlreadyTakenException();
            }
        }else if (color.equalsIgnoreCase("BLACK")){
            if (game.getBlackUsername() != null && !game.getBlackUsername().isEmpty()){
                throw new AlreadyTakenException();
            }
        }else {
            throw new BadRequestException();
        }

        dataAccess.joinGame(gameID, username, color);
    }

    public java.util.Collection<GameData> listGames(String token) throws ServiceException {
        AuthTokenData auth = dataAccess.getAuthToken(token);
        if (auth == null){
            throw new UnauthorizedException();
        }

        return dataAccess.getAllGames();
    }

    public GameData getGameState(String token, int gameID) throws ServiceException {
        var auth = dataAccess.getAuthToken(token);
        if(auth == null){
            throw new UnauthorizedException();
        }
        GameData game = dataAccess.getGame(gameID);
        if(game == null){
            throw new BadRequestException();
        }
        return game;
    }

    public void makeMove(String token, int gameID, chess.ChessMove move) throws ServiceException {
        AuthTokenData auth = dataAccess.getAuthToken(token);
        if (auth == null){
            throw new UnauthorizedException();
        }

        if (getGameState(token, gameID).isGameOver()){
            throw new BadRequestException();
        }

        GameData game = dataAccess.getGame(gameID);
        if (game == null){
            throw new BadRequestException();
        }

        var username = auth.username();

        var chessGame = game.getGame();
        var currentTeam = chessGame.getTeamTurn();

        boolean isWhite = username.equals(game.getWhiteUsername());
        boolean isBlack = username.equals(game.getBlackUsername());

        if((currentTeam == ChessGame.TeamColor.WHITE && !isWhite) ||
        (currentTeam == ChessGame.TeamColor.BLACK && !isBlack)){
            throw new ServiceException(400, "It is not your turn.");
        }

        try{
            chessGame.makeMove(move);
        }catch (Exception ex){
            throw new BadRequestException();
        }

        dataAccess.updateGame(gameID, chessGame);
    }

    public void resign(String token, int gameID) throws ServiceException {
        var game = dataAccess.getGame(gameID);
        AuthTokenData auth = dataAccess.getAuthToken(token);

        if (auth == null){
            throw new UnauthorizedException();
        }

        if (game == null){
            throw new BadRequestException();
        }

        if(game.isGameOver()){
            throw new ServiceException(400, "Game is over.");
        }

        String username = auth.username();

        if(!username.equals(game.getWhiteUsername()) && !username.equals(game.getBlackUsername())){
            throw new BadRequestException();
        }

        String winner;
        if(username.equals(game.getWhiteUsername())){
            winner = game.getBlackUsername();
        }else if (username.equals(game.getBlackUsername())){
            winner = game.getWhiteUsername();
        }else{
            throw new BadRequestException();
        }

        game.setGameOver(true);
        game.setWhiteUsername(winner);
        var chessGame = game.getGame();
        dataAccess.updateGame(gameID, chessGame);
    }

    public void leaveGame(String token, int gameID) throws ServiceException, DataAccessException {
        AuthTokenData auth = dataAccess.getAuthToken(token);
        if (auth == null){
            throw new UnauthorizedException();
        }

        GameData game = dataAccess.getGame(gameID);
        if (game == null){
            throw new BadRequestException();
        }

        var username = auth.username();
        boolean changed = false;

        if(username.equals(game.getWhiteUsername())){
            dataAccess.updatePlayers(gameID, null, game.getBlackUsername());
            changed = true;
        }

        if(username.equals(game.getBlackUsername())){
            dataAccess.updatePlayers(gameID, game.getWhiteUsername(), null);
            changed = true;
        }

        if(!changed){
            throw new BadRequestException();
        }
    }
}
