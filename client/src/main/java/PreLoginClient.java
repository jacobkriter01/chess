import java.util.Scanner;

import datamodel.*;
import exceptions.ServiceException;
import requests.*;
import responses.*;

import static ui.EscapeSequences.*;

public class PreLoginClient {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);

    public PreLoginClient(String url{
        this.server = new ServerFacade(url);
    }

    public String run(){
        System.out.println("Welcome to 240 Chess. Type help to get started.");

        while (true){
            System.out.print("> ");
            String input = scanner.nextLine().trim().toLowerCase();

            String[] parts = input.split(" ");
            String command = parts[0].toLowerCase();

            switch (command){
                case "help" -> printHelp();
                case "register" -> handleRegister(parts);
                case "login" -> {
                    String token = handleLogin(parts);
                    if(token != null){
                        return token;
                    }
                }
                case "quit" -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid input. Type 'help'");
            }
        }
    }


   private void printHelp(){
        System.out.println("""
                Commands:
                - register
                - login
                - quit
                """);
   }

   private String handleRegister(String[] parts){
        if (parts.length != 4){
            System.out.println("Usage: register <username> <password> <email>");
            return null;
        }

        String username = parts[1];
        String password = parts[2];
        String email = parts[3];

        try {
            var request = new RegisterRequest(username, password, email);
            RegisterResponse response = server.register(request);

            System.out.println("Logged in as " + response.username());
            return response.authToken();
        } catch (ServiceException e) {
            System.err.println(e.getMessage());
            return null;
        }
   }

   private String handleLogin(String[] parts){
       if (parts.length != 3){
           System.out.println("Usage: register <username> <password>");
           return null;
       }

       String username = parts[1];
       String password = parts[2];

       try{
           LoginRequest request = new LoginRequest(username, password);
           LoginResponse response = server.login(request);

           System.out.println("Logged in as " + response.username());
           return response.authToken();
       } catch (ServiceException ex){
           System.out.println(ex.getMessage());
           return null;
       }
   }


}
