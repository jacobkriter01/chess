package service;

import dataaccess.MemoryDataAccess;
import datamodel.AuthTokenData;
import datamodel.GameData;

public class GameService {
    private final MemoryDataAccess dataAccess;

    public GameService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String token, String gameName) throws Exception{
        AuthTokenData auth = dataAccess.getAuthToken(token);
        if(auth == null){
            throw new Exception("unauthorized");
        }
        return dataAccess.addGame(gameName, auth.username());
    }

    public void joinGame(String token, int gameID) throws Exception{
        AuthTokenData auth = dataAccess.getAuthToken(token);
        if(auth == null){
            throw new Exception("unauthorized");
        }
        dataAccess.joinGame(gameID, auth.username());
    }
}
