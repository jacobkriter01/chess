package server;

import dataaccess.MemoryDataAccess;
import datamodel.AuthTokenData;
import datamodel.GameData;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import service.UserService;
import service.GameService;
import exceptions.ServiceException;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final GameService gameService;
    private final MemoryDataAccess dataAccess;

    public Server() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);


        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", this::clearDatabase);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);
        server.get("game", this::listGames);

    }

    private void clearDatabase(Context ctx) {
        dataAccess.clear();
        ctx.status(200).result("{}");
    }

    private void register(Context ctx) { //handler
        var serializer = new Gson();
        try {
            var user = serializer.fromJson(ctx.body(), UserData.class);
            var registrationResponse = userService.register(user);

            ctx.result(serializer.toJson(registrationResponse));

        }catch (IllegalArgumentException ex){
            var errorMessage = String.format("{ \"message\": \"Error: %s\"}", ex.getMessage());
            ctx.status(400).result(errorMessage);
        }catch (Exception ex) {
            var errorMessage = String.format("{ \"message\": \"Error: %s\"}", ex.getMessage());
            ctx.status(403).result(errorMessage);
        }
    }

    public void login(Context ctx){
        var serializer = new Gson();
        try{
            var user = serializer.fromJson(ctx.body(), UserData.class);

            if(user.username() == null  || user.password() == null){
                ctx.status(400).result("{ \"message\": \"Error: %s\"}");
                return;
            }

            var loginResponse = userService.login(user);
            ctx.status(200).result(serializer.toJson(loginResponse));
        }catch (Exception ex){
            String errorMessage = ex.getMessage();
            if(errorMessage.equals("unauthorized")){
                ctx.status(401).result("{ \"message\": \"Error: %s\"}");
            } else {
                ctx.status(400).result("{ \"message\": \"Error: %s\"}");
            }

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
        } catch (Exception ex) {
            ctx.status(401).result("{ \"message\": \"Error: " + ex.getMessage() + "\" }");
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
            ctx.status(200).result("{ \"gameID\": " + game.id() + "}");
        }catch (Exception ex){
            ctx.status(401).result("{ \"message\": \"Error: " + ex.getMessage() + "\" }");
        }
    }

    public void joinGame(Context ctx){
        var serializer = new Gson();
        try{
            var token  = ctx.header("Authorization");
            var body = serializer.fromJson(ctx.body(), java.util.Map.class);
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

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
