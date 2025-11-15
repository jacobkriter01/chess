import client.ServerFacade;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import responses.*;
import exceptions.ServiceException;

import java.util.Scanner;

public class PostLoginClient {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);
    private final State state = State.POSTLOGIN;

    public PostLoginClient(String url) {
        this.server = new ServerFacade(url);
    }

    public void run(String authToken){
        while (true) {
            System.out.print(state + ">>> ");
            String input = scanner.nextLine().trim().toLowerCase();

            String[] parts = input.split(" ");
            String command = parts[0].toLowerCase();

            switch (command){
                case "help" -> printHelp();
                case "list" -> listGames(authToken);
                case "create" -> createGame(authToken, parts);
                case "join" -> joinGame(authToken, parts);
                case "observe" -> observeGame(authToken, parts);
                case "logout" -> {
                    logout(authToken);
                    return;
                }
                default -> System.out.println("Invalid input. Type 'help'");
            }
        }
    }

    private void printHelp(){
        System.out.println("""
                    Commands:
                    - list
                    - create
                    - join
                    - observe
                    - logout
                    """);
    }

    private void listGames(String authToken){
        try{
            ListGamesResponse response = server.listGames(authToken);
            System.out.println("Games:");
            for (var g : response.games()){
                System.out.println("- " + g);
            }
        }catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    private void createGame(String authToken, String[] parts){
        if (parts.length != 2){
            System.out.println("Usage: create <game name>");
            return;
        }

        String gameName = parts[1];

        try{
            var request = new CreateGameRequest(gameName);
            CreateGameResponse response = server.createGame(authToken, request);
            System.out.println("Created game: " + response.gameID());
        } catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    private void joinGame(String authToken, String[] parts){
        if (parts.length != 3){
            System.out.println("Usage: join <game ID> [BLACK][WHITE]");
            return;
        }

        try{
            int gameID = Integer.parseInt(parts[1]);
            String color = parts[2].toLowerCase();

            JoinGameRequest request = new JoinGameRequest(authToken, gameID, color);
            JoinGameResponse response = server.joinGame(authToken, request);

            System.out.println("Joined game " + gameID +" as "+ color);

            GamePlayClient gamePlayClient = new GamePlayClient(color);
            gamePlayClient.run();
        }catch (NumberFormatException ex){
            System.out.println("Game ID must be a number");
        }catch (ServiceException ex){
            System.out.println("Whoops" + ex.getMessage());
        }
    }

    private void observeGame(String authToken, String[] parts){
        if (parts.length != 2){
            System.out.println("Usage: observe <game ID>");
            return;
        }
        try{
            int gameID = Integer.parseInt(parts[1]);

            JoinGameRequest request = new JoinGameRequest(authToken, gameID, "WHITE");
            JoinGameResponse response = server.joinGame(authToken, request);

            System.out.println("Observing game: " + gameID);

            GamePlayClient gamePlayClient = new GamePlayClient("WHITE");
            gamePlayClient.run();
        }catch (NumberFormatException ex){
            System.out.println("Game ID must be a number");
        }catch (ServiceException ex){
            System.out.println("Whoops" + ex.getMessage());
        }
    }

    private void logout(String authToken){
        try{
            server.logout(authToken);
            System.out.println("Logged out");
        }catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }
}
