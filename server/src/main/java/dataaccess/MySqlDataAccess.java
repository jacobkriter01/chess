package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.AuthTokenData;
import datamodel.GameData;
import datamodel.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySqlDataAccess implements DataAccess {

    private final Gson gson = new Gson();

    public MySqlDataAccess(){
        try {
            DatabaseManager.createDatabase();
            createTables();
        } catch(DataAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void createTables() throws DataAccessException {
        String[] statements = {
                """
CREATE TABLE IF NOT EXISTS users(
username VARCHAR(50) PRIMARY KEY,
password VARCHAR(250) NOT NULL)
""",
                """
CREATE TABLE IF NOT EXISTS auth_tokens (
token VARCHAR(255)  PRIMARY KEY,
username VARCHAR(50) NOT NULL,
FOREIGN KEY (username) REFERENCES users(username))
""",
                """
CREATE TABLE IF NOT EXISTS games (
id INT AUTO_INCREMENT PRIMARY KEY,
gameName VARCHAR(100) NOT NULL,
whiteUsername VARCHAR(50),
blackUsername VARCHAR(50),
gameState TEXT,
FOREIGN KEY (whiteUsername) REFERENCES users(username),
FOREIGN KEY (blackUsername) REFERENCES users(username))
"""};


        try (var conn = DatabaseManager.getConnection()){
            for(var statement : statements){
                try (var ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e){
            throw new DataAccessException("Unable to create tables.", e);
        }
    }

    @Override
    public void clear() {
        try(var conn = DatabaseManager.getConnection();
            var stmt = conn.createStatement()){
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=0;");

            stmt.executeUpdate("DELETE FROM auth_tokens");
            stmt.executeUpdate("DELETE FROM games");
            stmt.executeUpdate("DELETE FROM users");

            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=1;");
        } catch (SQLException e){
            throw new RuntimeException("Unable to clear tables.", e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addUser(UserData user) {
        var sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)){
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

            ps.setString(1, user.username());
            ps.setString(2, hashedPassword);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Unable to insert user.", e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public UserData getUser(String username) {
        var sql = "SELECT username, password FROM users WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            try(var rs = ps.executeQuery()){
                if(rs.next()){
                    return new UserData(rs.getString("username"),null, rs.getString("password"));
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Unable to retrieve user", e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void addAuthToken(AuthTokenData authToken) {
        var sql = "INSERT INTO auth_tokens (token, username) VALUES (?, ?)";
        try(var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(sql)){
            ps.setString(1, authToken.authToken());
            ps.setString(2, authToken.username());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't add auth token",e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthTokenData getAuthToken(String token) {
        var sql = "SELECT token, username FROM auth_tokens WHERE token = ?";
        try(var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(sql)){
            ps.setString(1, token);
            try(var rs = ps.executeQuery()){
                if(rs.next()){
                    return new AuthTokenData(rs.getString("token"), rs.getString("username"));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Unable to retrieve auth token",e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void removeAuthToken(String token) {
        var sql = "DELETE FROM auth_tokens WHERE token = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)){
            ps.setString(1, token);
            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Unable to remove auth token",e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public GameData addGame(String gameName) {
        ChessGame initialGame = new ChessGame();
        String gameJson = gson.toJson(initialGame);

        var sql = "INSERT INTO games (gameName, gameState, whiteUsername, blackUsername) VALUES (?, ?, NULL, NULL)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql, RETURN_GENERATED_KEYS)){
            ps.setString(1, gameName);
            ps.setString(2, gameJson);
            ps.executeUpdate();
            try(var rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    int id = rs.getInt(1);
                    return new GameData(id, gameName, null, null, initialGame);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Unable to add game.", e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public GameData getGame(int gameId) {
        var sql = "SELECT * FROM games WHERE id = ?";
        try(var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)){
            ps.setInt(1,gameId);
            try (var rs = ps.executeQuery()){
                if(!rs.next()) {
                    return null;
                }
                var whiteUsername = rs.getString("whiteUsername");
                var blackUsername = rs.getString("blackUsername");
                var gameStateJson = rs.getString("gameState");
                ChessGame game = null;
                if (gameStateJson != null && !gameStateJson.isBlank()) {
                    try{
                        game = gson.fromJson(gameStateJson, ChessGame.class);
                    } catch (Exception ex) {
                        game = new ChessGame();
                    }
                } else{
                    game = new ChessGame();
                }
                return new GameData(rs.getInt("id"), rs.getString("gameName"), whiteUsername, blackUsername, game);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve game.", e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void joinGame(int gameId, String username, String color) {
        String column = color.equalsIgnoreCase("WHITE") ? "whiteUsername" : "blackUsername";
        var sql = "UPDATE games SET " + column + " =? WHERE id=?";
        try(var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            ps.setInt(2, gameId);
            int rowsAffected = ps.executeUpdate();
            if(rowsAffected == 0){
                throw new RuntimeException("Invalid game ID" +  gameId);
            }
        }catch (SQLException e){
            throw new RuntimeException("Unable to join game.", e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<GameData> getAllGames() {
        var sql = "SELECT * FROM games";
        var games = new ArrayList<GameData>();
        try(var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()){
            while(rs.next()){
                var whiteUsername = rs.getString("whiteUsername");
                var blackUsername = rs.getString("blackUsername");
                var gameStateJson = rs.getString("gameState");
                ChessGame game = null;
                if (gameStateJson != null && !gameStateJson.isBlank()) {
                    try{
                        game = gson.fromJson(gameStateJson, ChessGame.class);
                    }catch (Exception ex) {
                        game = new ChessGame();
                    }
                }else{
                    game = new ChessGame();
                }
                games.add(new GameData(rs.getInt("id"), rs.getString("gameName"), whiteUsername, blackUsername, game));
            }
        }catch (SQLException e){
            throw new RuntimeException("Unable to retrieve games.", e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return games;
    }
}

