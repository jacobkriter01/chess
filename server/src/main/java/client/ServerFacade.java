package client;

import com.google.gson.Gson;
import exceptions.ServiceException;
import datamodel.UserData;
import datamodel.GameData;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.Provider;
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

    public int createGame(String authToken, String gameName) throws ServiceException {
        var body = Map.of("gamaName", gameName);
        var request = buildRequest("POST", "/game", body, authToken);
        var response = sendRequest(request);

        Map result = handleResponse(response, Map.class);
        Double id = (Double) result.get("id");
        return id.intValue();
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws ServiceException {
        var body = Map.of("gameID", gameID, "playerColor", playerColor);
        var request = buildRequest("PUT", "/game", body, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public Map listGames(String authToken) throws ServiceException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, Map.class);
    }

    public void clearDB() throws ServiceException {
        var request = buildRequest("DELETE", "/game", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
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
        return BodyPublishers.ofString((new Gson()).toJson(request));
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ServiceException {
        try{
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ServiceException(500, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> type) throws ServiceException {
        int status = response.statusCode();

        if (status / 100 != 2){
            var body = response.body();
            throw new ServiceException(status, extractMessage(body));
        }

        if (type == null) {
            return null;
        }

        return new Gson().fromJson(response.body(), type);
    }

    private String extractMessage(String jsonError) {
        try{
            Map m = new Gson().fromJson(jsonError, Map.class);
            return(String) m.get("message");
        } catch (Exception ex){
            return "unknown error";
        }
    }
}
