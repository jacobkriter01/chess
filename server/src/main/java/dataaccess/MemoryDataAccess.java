package dataaccess;

import datamodel.AuthTokenData;
import datamodel.GameData;
import datamodel.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, AuthTokenData> authTokens = new HashMap<>();
    private HashMap<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;

    @Override
    public void clear() {
        users.clear();
        authTokens.clear();
        games.clear();
        nextId = 1;
    }

    @Override
    public void addUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void addAuthToken(AuthTokenData authToken) {
        authTokens.put(authToken.authToken(), authToken);
    }

    @Override
    public AuthTokenData getAuthToken(String token) {
        return authTokens.get(token);
    }

    @Override
    public void removeAuthToken(String token) {
        authTokens.remove(token);
    }

    public GameData addGame(String gameName) {
        GameData game = new GameData(nextId++, gameName, null, null, null, false, null);
        games.put(game.getGameID(), game);
        return game;
    }

    public void joinGame(int gameID, String username, String color) {
        GameData game = games.get(gameID);
        if (game == null) {
            return;
        }
        if (color.equalsIgnoreCase("WHITE")) {
            game.setWhiteUsername(username);
        } else if (color.equalsIgnoreCase("BLACK")) {
            game.setBlackUsername(username);
        }

    }

    public GameData getGame(int gameId) {
        return games.get(gameId);
    }

    public java.util.Collection<GameData> getAllGames() {
        return games.values();
    }
}
