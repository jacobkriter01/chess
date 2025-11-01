package dataaccess;

import com.google.gson.Gson;
import datamodel.AuthTokenData;
import datamodel.GameData;
import datamodel.UserData;
import exceptions.*;

import javax.xml.crypto.Data;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess {

    private final Gson gson = new Gson();

    public MySqlDataAccess() throws ServiceException {
        try {
            DatabaseManager.createDatabase();
            createTabbles();
        } catch(DataAccessException ex){

        }
    }

    private void createTabbles() throws DataAccessException {
        String[] statements = {
                """
CREATE TABLES IF NOT EXISTS users(
username VARCHAR(50) PRIMARY KEY,
password VARCHAR(250) NOT NULL)
""",
                """
CREATE TABLE IF NOT EXISTS auth_tokens (
token VARCHAR(255)  PRIMARY KEY,
username VARCHAR(50) NOT NULL,
FORGEIN KEY (username) REFERENCES users(username))
""",
                """
CREATE TABLE IF NOT EXISTS games (
id INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(100) NOT NULL,
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
            throw new DataAccessException("Unable to create tabbles.", e);
        }
    }

    @Override
    public void clear() {
        try(var conn = DatabaseManager.getConnection();
            var stmt = conn.createStatement()){
            stmt.executeUpdate("TRUNCATE TABLE auth_tokens");
            stmt.executeUpdate("TRUNCATE TABLE users");
            stmt.executeUpdate("TRUNCATE TABLE games");
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
            ps.setString(1, user.username());
            ps.setString(2, user.password());
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
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
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
        var sql = "INSERT INTO auth_tokens (authToken, username) VALUES (?, ?)";
        try(var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(sql)){
            ps.setString(1, authToken.authToken());
            ps.setString(2, authToken.authToken());
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
        var sql = "INSERT INTO games (gameName) VALUES (?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql, RETURN_GENERATED_KEYS)){
            ps.setString(1, gameName);
            ps.executeUpdate();
            try(var rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    int id = rs.getInt(1);
                    return new GameData(id, gameName, null, null);
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
                if(rs.next()){
                    var whiteUsername = rs.getString("whiteUsername");
                    var blackUsername = rs.getString("blackUsername");
                    return new GameData(rs.getInt("id"), rs.getString("name"), whiteUsername, blackUsername);
                };
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve game.", e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}

