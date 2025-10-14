package server;

import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService = new UserService();

    public Server() {


        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);



    }

    private void register(Context ctx) { //handler
        var serializer = new Gson();
        var user = serializer.fromJson(ctx.body(), UserData.class);

        var registrationResponse = userService.register(user);

        ctx.result(serializer.toJson(registrationResponse));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
