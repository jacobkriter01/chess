package service;

import dataaccess.MySqlDataAccess;
import datamodel.GameData;
import datamodel.UserData;

import exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {

    private MySqlDataAccess dataAccess;
    private UserService userService;
    private GameService gameService;

    private UserData newUser;

    @BeforeEach
    void setUp() {
        dataAccess = new MySqlDataAccess();
        dataAccess.clear();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        newUser = new UserData("jacob", "jacob@mail.com", "pwd");
    }

    @Test
    void registerUser() throws ServiceException {
        var response = userService.register(newUser);

        assertNotNull(response);
        assertEquals("jacob", response.username());
        assertNotNull(response.authToken());

        assertNotNull(dataAccess.getUser("jacob"));
        assertNotNull(dataAccess.getAuthToken(response.authToken()));
    }

    @Test
    void failDuplicate() throws ServiceException {
        userService.register(newUser);
        assertThrows(AlreadyTakenException.class, () -> userService.register(newUser));
    }

    @Test
    void failBadUser() {
        assertThrows(BadRequestException.class, () -> userService.register(new UserData(null,"jacob@mail", "pwd")));
    }

    @Test
    void loginSuccess() throws ServiceException {
        userService.register(newUser);
        var token = userService.login(newUser);

        assertNotNull(token);
        assertEquals("jacob", token.username());
        assertNotNull(dataAccess.getAuthToken(token.authToken()));
    }

    @Test
    void loginFail() throws ServiceException {
        userService.register(newUser);
        var wrongUser = new UserData("maddie", "maddie@mail.com", "wrongpassword");

        assertThrows(UnauthorizedException.class, () -> userService.login(wrongUser));
    }

    @Test
    void logoutSuccess() throws ServiceException {
        var reg = userService.register(newUser);
        var token = reg.authToken();

        userService.logout(token);

        assertNull(dataAccess.getAuthToken((token)));
    }

    @Test
    void logoutFail() {
        assertThrows(UnauthorizedException.class, () -> userService.logout("fake token"));
    }

    @Test
    void createGame() throws ServiceException {
        var reg = userService.register(newUser);
        var token = reg.authToken();

        GameData game = gameService.createGame(token, "test game");

        assertNotNull(game);
        assertEquals("test game", game.getGameName());
        assertNotNull(dataAccess.getGame(game.getGameID()));
    }

    @Test
    void createGameFailUnauth(){
        assertThrows(UnauthorizedException.class, () -> gameService.createGame("fake token", "test game"));
    }

    @Test
    void createGameFailBadRequest() throws ServiceException {
        var reg = userService.register(newUser);
        var token = reg.authToken();

        assertThrows(BadRequestException.class, () -> gameService.createGame(token, ""));
    }

    @Test
    void joinGameSuccess() throws ServiceException {
        var reg = userService.register(newUser);
        var token = reg.authToken();

        var game =  gameService.createGame(token, "test game");
        gameService.joinGame(token, game.getGameID(), "WHITE");

        var updated = dataAccess.getGame(game.getGameID());
        assertEquals("jacob", updated.getWhiteUsername());
    }

    @Test
    void joinGameFailBadRequest() throws ServiceException {
        var reg = userService.register(newUser);
        var token = reg.authToken();

        assertThrows(BadRequestException.class, () -> gameService.joinGame(token, 123, "WHITE"));
    }

    @Test
    void joinGameFailUnauth(){
        assertThrows(UnauthorizedException.class, () -> gameService.joinGame("bad token", 123, "WHITE"));
    }

    @Test
    void joinGameFailAlreadyTaken() throws ServiceException {
        var reg1 = userService.register(newUser);
        var token1 = reg1.authToken();
        var game = gameService.createGame(token1, "test game");
        gameService.joinGame(token1, game.getGameID(), "WHITE");

        var reg2 = userService.register(new UserData("maddie", "maddie@mail.com", "pwd"));
        var token2 = reg2.authToken();

        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(token2, game.getGameID(), "WHITE"));
    }

    @Test
    void listGamesSuccess() throws ServiceException {
        var reg = userService.register(newUser);
        var token = reg.authToken();
        gameService.createGame(token, "test game");
        gameService.createGame(token, "test game2");

        Collection<GameData> games = gameService.listGames(token);

        assertEquals(2, games.size());
    }

    @Test
    void listGamesFailUnauth(){
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("bad token"));
    }
}