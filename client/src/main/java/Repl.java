import client.ServerFacade;

public class Repl {
    private final PreLoginClient preLogin;
    private final PostLoginClient postLogin;
    private final GamePlayClient gamePlay;

    private State state = State.PRELOGIN;

    public Repl(String serverUrl){
        ServerFacade server = new ServerFacade(serverUrl);

        preLogin = new PreLoginClient(server, this);
        postLogin = new PostLoginClient(server, this);
        gamePlay = new GamePlayClient(server, this);
    }
}
