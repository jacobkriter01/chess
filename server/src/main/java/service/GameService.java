package service;

import dataaccess.MemoryDataAccess;
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
}
