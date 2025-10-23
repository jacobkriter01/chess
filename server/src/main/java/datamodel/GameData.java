package datamodel;

import java.util.Set;
import java.util.HashSet;

public class GameData {
    private int id;
    private String name;
    private String creator;
    private Set<String> players = new HashSet<>();

    public GameData(int id, String name, String creator) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.players.add(creator);
    }

    public int id(){
        return id;
    }
    public String name(){
        return name;
    }
    public String creator(){
        return creator;
    }
    public Set<String> players(){
        return players;
    }

    public void addPlayer(String username){
        players.add(username);
    }
}
