import java.util.Scanner;

public class Repl {
    private final String serverUrl;

    public Repl(String serverUrl){
        this.serverUrl = serverUrl;
    }

    public void run(){
        PreLoginClient preLoginClient = new PreLoginClient(serverUrl);
        String authToken = preLoginClient.run();

        PostLoginClient postLoginClient = new PostLoginClient(serverUrl);
        postLoginClient.run(authToken);

    }
}
