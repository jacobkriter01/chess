package client;

import com.google.gson.Gson;
import exceptions.ServiceException;
import datamodel.UserData;
import datamodel.GameData;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public Map register(UserData user) throws ServiceException {
        var request = buildRequest("POST", "/user", user, null);
        var response = sendRequest(request);
        return handleResponse(response, Map.class);
    }

    public Map login(UserData user) throws ServiceException {
        var request = buildRequest("POST", "/session", user, null);
        var response = sendRequest(request);
        return handleResponse(response, Map.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));

        if (authToken != null) {
            builder.header("Authorization", authToken);
        }

        if (body != null) {
            builder.header("Content-Type", "application/json");
        }
        return builder.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request == null){
            return BodyPublishers.noBody();
        }
    }
}
