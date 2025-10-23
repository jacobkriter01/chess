package server;

import dataaccess.MemoryDataAccess;
import datamodel.AuthTokenData;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import service.UserService;

public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        var dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);


        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);



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
        var serializer = new Gson();
        try{
            AuthTokenData tokenData = serializer.fromJson(ctx.body(), AuthTokenData.class);
        if(tokenData == null || tokenData.authToken() == null || tokenData.authToken().isEmpty()) {
            ctx.status(401).result("{ \"message\": \"Error: %s\"}");
            return;
        }

            userService.logout(tokenData.authToken());
            ctx.status(200).result("{}");
        } catch (Exception ex){
            ctx.status(401).result("{ \"message\": \"Error: %s\"}");
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
