package client;

import exceptions.ServiceException;
import org.junit.jupiter.api.*;
import server.Server;
import client.ServerFacade;

import requests.*;
import responses.*;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        String url = "http://localhost:" + port;
        facade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void resetDB() throws Exception {
        facade.clearDb();
    }


    @Test
    public void registerPositive() throws Exception {
        var request = new RegisterRequest("jacob", "pwd", "email");
        var response = facade.register(request);

        assertNotNull(response.authToken());
    }

    @Test
    public void registerNegative() throws Exception {
        var request = new RegisterRequest("jacob", "pwd", "email");
        facade.register(request);

        assertThrows(ServiceException.class, () -> facade.register(request));
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register(new RegisterRequest("jacob", "pwd", "email"));
        var response = facade.login(new LoginRequest("jacob", "pwd"));
        assertNotNull(response.authToken());
    }

    @Test
    public void loginNegative() throws Exception {
        facade.register(new RegisterRequest("jacob", "pwd", "email"));
        assertThrows(ServiceException.class, () -> facade.login(new LoginRequest("jacob", "wrong")));
    }

    @Test
    public void logoutPositive() throws Exception {
        facade.register(new RegisterRequest("jacob", "pwd", "email"));
        String token = facade.login(new LoginRequest("jacob", "pwd")).authToken();

        assertDoesNotThrow(() -> facade.logout(token));
    }

    @Test
    public void logoutNegative() throws Exception {
        assertThrows(ServiceException.class, () -> facade.logout("wrong"));
    }

    @Test
    public void createGamePositive() throws Exception {
        facade.register(new RegisterRequest("jacob", "pwd", "email"));
        String token = facade.login(new LoginRequest("jacob", "pwd")).authToken();

        var response = facade.createGame(token, new CreateGameRequest("coolGame"));

        assertTrue(response.gameID() > 0);
    }

    @Test
    public void createGameNegative() throws Exception {
        assertThrows(ServiceException.class, () -> facade.createGame("wrong", new CreateGameRequest("coolGame")));
    }

    @Test
    public void listGamePositive() throws Exception {
        facade.register(new RegisterRequest("jacob", "pwd", "email"));
        String token = facade.login(new LoginRequest("jacob", "pwd")).authToken();

        facade.createGame(token, new CreateGameRequest("coolGame"));
        facade.createGame(token, new CreateGameRequest("dumbGame"));

        var response = facade.listGames(token);

        assertEquals(2, response.games().toArray().length);
    }

    @Test
    public void listGameNegative() throws Exception {
        assertThrows(ServiceException.class, () -> facade.listGames("wrong"));
    }

    @Test
    public void joinGamePositive() throws Exception {
        facade.register(new RegisterRequest("jacob", "pwd", "email"));
        String token = facade.login(new LoginRequest("jacob", "pwd")).authToken();

        var game =  facade.createGame(token, new CreateGameRequest("coolGame"));
        var request = new JoinGameRequest(token, game.gameID(), "WHITE");

        assertDoesNotThrow(() -> facade.joinGame(token, request));
    }

    @Test
    public void joinGameNegative() throws Exception {
        facade.register(new RegisterRequest("jacob", "pwd", "email"));
        String token = facade.login(new LoginRequest("jacob", "pwd")).authToken();

        var request = new JoinGameRequest(token, 9999, "BLACK");
        assertThrows(ServiceException.class, () -> facade.joinGame(token, request));
    }
}
