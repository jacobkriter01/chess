package server;

import dataaccess.MySqlDataAccess;
import datamodel.*;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import service.UserService;
import service.GameService;
import exceptions.ServiceException;
import chess.ChessGame;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final GameService gameService;
    private final MySqlDataAccess dataAccess;

    private final Map<WsContext, Integer> clientGames = new ConcurrentHashMap<>();
    private final Map<WsContext, String> clientTokens = new ConcurrentHashMap<>();

    public Server() {
        dataAccess = new MySqlDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);


        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("/db", this::clearDatabase);
        server.post("/user", this::register);
        server.post("/session", this::login);
        server.delete("/session", this::logout);
        server.post("/game", this::createGame);
        server.put("/game", this::joinGame);
        server.get("/game", this::listGames);
        server.get("/game/{id}", this::getGameState);

        server.ws("/connect", ws -> {
            ws.onConnect(ctx ->{
                System.out.println("WebSocket connected.");
            });

            ws.onMessage(ctx -> {
                var cmd = new Gson().fromJson(ctx.message(), websocket.commands.UserGameCommand.class);
                handleWsCommand(ctx, cmd);
            });

            ws.onClose(ctx -> {
                clientGames.remove(ctx);
                clientTokens.remove(ctx);
                System.out.println("WebSocket closed.");
            });
        });
    }

    private void handleWsCommand(WsContext ctx, websocket.commands.UserGameCommand cmd) {
        try{
            String token = cmd.getAuthToken();
            Integer gameID = cmd.getGameID();

            switch(cmd.getCommandType()){
                case CONNECT ->{
                    clientTokens.put(ctx, token);
                    clientGames.put(ctx, gameID);
                    var state = gameService.getGameState(token, gameID);
                    String username = findUsername(token);
                    String color = findColor(state, token);


                    var msg = websocket.messages.ServerMessage.loadGame(state.getGame());
                    ctx.send(new Gson().toJson(msg));

                    broadcastToGame(gameID, websocket.messages.ServerMessage.notification(username + " joined the game as " + color));


                }

                case MAKE_MOVE -> {
                    gameService.makeMove(token, gameID, cmd.getMove());

                    var state = gameService.getGameState(token, gameID);
                    ChessGame game = state.getGame();
                    var turn = game.getTeamTurn();
                    String username = findUsername(token);
                    broadcastToGame(gameID, websocket.messages.ServerMessage.loadGame(state.getGame()));
                    broadcastToGame(gameID, websocket.messages.ServerMessage.notification(username + " moved: " + cmd.getMove()));

                    if(game.isInCheck(turn)){
                        broadcastToGame(gameID, websocket.messages.ServerMessage.notification(turn + " is in check"));
                    }
                    if(game.isInCheckmate(turn)){
                        broadcastToGame(gameID, websocket.messages.ServerMessage.notification(turn + " is in checkmate"));
                    }
                }

                case LEAVE -> {
                    clientGames.remove(ctx);
                    clientTokens.remove(ctx);
                    gameService.leaveGame(token, gameID);
                    broadcastToGame(gameID, websocket.messages.ServerMessage.notification(findUsername(token) + " left the game."));
                }

                case RESIGN -> {
                    gameService.resign(token, gameID);
                    broadcastToGame(gameID, websocket.messages.ServerMessage.notification(findUsername(token) + " resigned."));
                    broadcastToGame(gameID, websocket.messages.ServerMessage.notification(gameService.getGameState(token, gameID).getWinner() + " won the game!"));
                }
            }
        } catch (Exception ex) {
            var msg = websocket.messages.ServerMessage.error(ex.getMessage());
            ctx.send(new Gson().toJson(msg));
        }
    }

    private void broadcastToGame(int gameID, websocket.messages.ServerMessage msg) {
        String json = new Gson().toJson(msg);

        for (var entry : clientGames.entrySet()) {
            WsContext ws = entry.getKey();
            int g = entry.getValue();
            if(g == gameID){
                ws.send(json);
            }
        }
    }

    private void clearDatabase(Context ctx) {
        try{
            dataAccess.clear();
            ctx.status(200).result("{}");
        } catch (Throwable ex) {
            var message = String.format("{ \"message\": \"Error: %s\"}", ex.getMessage());
            ctx.status(500).result(message);
        }
    }

    private void register(Context ctx) { //handler
        var serializer = new Gson();
        try {
            var user = serializer.fromJson(ctx.body(), UserData.class);
            var registrationResponse = userService.register(user);

            ctx.result(serializer.toJson(registrationResponse));

        }catch (ServiceException ex){
            ctx.status(ex.getStatusCode()).result("{ \"message\": \"Error: " + ex.getMessage() + "\" }");
        }catch (Exception ex) {
            var errorMessage = String.format("{ \"message\": \"Error: %s\"}", ex.getMessage());
            ctx.status(500).result(errorMessage);
        }
    }

    public void login(Context ctx){
        var serializer = new Gson();
        try {
            var user = serializer.fromJson(ctx.body(), UserData.class);

            if (user.username() == null || user.password() == null) {
                ctx.status(400).result("{ \"message\": \"Error: %s\"}");
                return;
            }

            var loginResponse = userService.login(user);
            ctx.status(200).result(serializer.toJson(loginResponse));
        }catch (ServiceException ex){
            ctx.status(ex.getStatusCode()).result("{ \"message\": \"Error: " + ex.getMessage() + "\" }");
        }catch (Exception ex){
            String errorMessage = String.format("{ \"message\": \"Error: %s\"}",ex.getMessage());
            ctx.status(500).result(errorMessage);
        }
    }

    public void logout(Context ctx){
        try {
            var token = ctx.header("Authorization");

            if(token == null || token.isEmpty()){
                ctx.status(401).result("{ \"message\": \"Error: Missing auth token\" }");
            }

            userService.logout(token);
            ctx.result("{}");
        } catch (ServiceException ex){
            ctx.status(ex.getStatusCode()).result("{ \"message\": \"Error: " + ex.getMessage() + "\" }");
        }catch (Exception ex){
            String errorMessage = String.format("{ \"message\": \"Error: %s\"}",ex.getMessage());
            ctx.status(500).result(errorMessage);
        }
    }

    public void createGame(Context ctx){
        var serializer = new Gson();
        try{
            var token = ctx.header("Authorization");
            var body = serializer.fromJson(ctx.body(), java.util.Map.class);
            var name = (String)body.get("gameName");
            if(name == null){
                ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
                return;
            }
            var game = gameService.createGame(token, name);
            ctx.status(200).result("{ \"gameID\": " + game.getGameID() + "}");
        }catch (ServiceException ex){
            ctx.status(ex.getStatusCode()).result("{ \"message\": \"Error: " + ex.getMessage() + "\" }");
        }catch (Exception ex){
            String errorMessage = String.format("{ \"message\": \"Error: %s\"}",ex.getMessage());
            ctx.status(500).result(errorMessage);
        }
    }

    public void joinGame(Context ctx){
        var serializer = new Gson();
        try{
            var token  = ctx.header("Authorization");
            var body = serializer.fromJson(ctx.body(), java.util.Map.class);
            if (body.get("gameID") == null || body.get("playerColor") == null){
                ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
                return;
            }
            var playerColor = (String)body.get("playerColor");
            var gameID = ((Double)body.get("gameID")).intValue();

            gameService.joinGame(token, gameID, playerColor);
            ctx.status(200).result("{}");

        } catch (ServiceException ex){
            ctx.status(ex.getStatusCode()).result("{ \"message\": \"Error: " + ex.getMessage() + "\" }");
        } catch (Exception ex){
            ctx.status(500).result("{ \"message\": \"Error: " + ex.getMessage() + "\" }");
        }
    }

    public void listGames(Context ctx){
        var serializer = new Gson();
        try{
            var token = ctx.header("Authorization");
            var games = gameService.listGames(token);
            var response =  new java.util.HashMap<String, Object>();
            response.put("games", games);
            ctx.status(200).result(serializer.toJson(response));

        } catch (ServiceException ex){
            ctx.status(ex.getStatusCode()).result("{ \"message\": \"Error: " + ex.getMessage() + "\" }");
        } catch (Exception ex){
            ctx.status(500).result("{ \"message\": \"Error: " + ex.getMessage() + "\" }");
        }
    }

    public void getGameState(Context ctx){
        var serializer = new Gson();
        try{
            var token = ctx.header("Authorization");
            if(token == null || token.isEmpty()){
                ctx.status(401).result("{ \"message\": \"Error: missing auth token\" }");
                return;
            }

            String idParam = ctx.pathParam("id");
            int id;
            try{
                id = Integer.parseInt(idParam);
            }catch (NumberFormatException e){
                ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
                return;
            }

            var gameData = gameService.getGameState(token, id);
            var response = new java.util.HashMap<String, Object>();
            response.put("gameID", gameData.getGameID());
            response.put("whiteUsername", gameData.getWhiteUsername());
            response.put("blackUsername", gameData.getBlackUsername());

            ChessGame chessGame = gameData.getGame();
            response.put("board", chessGame.getBoard());
            response.put("turn", chessGame.getTeamTurn());
            response.put("gameOver", false);

            ctx.status(200).result(serializer.toJson(response));
        }catch (ServiceException ex){
            ctx.status(ex.getStatusCode()).result("{ \"message\": \"Error: " + ex.getMessage() + "\" }");
        }catch (Exception ex){
            ctx.status(500).result("{ \"message\": \"Error: " + ex.getMessage() + "\" }");
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }

    public String findUsername(String token){
        return dataAccess.getAuthToken(token).username();
    }

    public String findColor(GameData game, String token){
        String username  = findUsername(token);
        if (username.equals(game.getWhiteUsername())){
            return "white";
        }
        if (username.equals(game.getBlackUsername())){
            return "black";
        }
        return "observer";
    }
}
