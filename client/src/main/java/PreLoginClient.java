public class PreLoginClient {
    private final ServerFacade server;
    private final Repl repl;

    public PreLoginClient(ServerFacade server, Repl repl){
        this.server = server;
        this.repl = repl;
    }

    public String eval(String input){
        String [] tokens = input.split(" ");
        String command = tokens[0];

        return switch (command){
            case "register" -> register(tokens);
            case "login" -> login(tokens);
            default -> help();
        };
    }


    private String register(String[] tokens){
        if(tokens.length != 4){
            return "Invalid input";
        }
        var result = server.register(tokens[1], tokens[2], tokens[3]);
        return "logged in as " + result.username();
    }
}
