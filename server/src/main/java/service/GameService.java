package service;

import dataaccess.MemoryDataAccess;
import datamodel.AuthTokenData;
import datamodel.GameData;
import exceptions.AlreadyTakenExcpetion;
import exceptions.BadRequestException;
import exceptions.ServiceException;
import exceptions.UnauthorizedException;

public class GameService {
    private final MemoryDataAccess dataAccess;

    public GameService(MemoryDataAccess dataAccess) {
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
        return dataAccess.addGame(gameName, auth.username());
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

        if (color.equalsIgnoreCase("WHITE")){
            if (game.whiteUsername() != null && !game.whiteUsername().isEmpty()){
                throw new AlreadyTakenExcpetion();
            }
            game.setWhiteUsername(username);
        }else if (color.equalsIgnoreCase("BLACK")){
            if (game.blackUsername() != null && !game.blackUsername().isEmpty()){
                throw new AlreadyTakenExcpetion();
            }
            game.setBlackUsername(username);
        }else {
            throw new BadRequestException();
        }
    }

    public java.util.Collection<GameData> listGames(String token) throws ServiceException {
        AuthTokenData auth = dataAccess.getAuthToken(token);
        if (auth == null){
            throw new UnauthorizedException();
        }

        return dataAccess.getAllGames();
    }
}
