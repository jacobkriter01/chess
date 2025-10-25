package datamodel;

public class GameData {
    private int id;
    private String name;
    private String whiteUsername;
    private String blackUsername;

    public GameData(int id, String name, String creator) {
        this.id = id;
        this.name = name;
        this.whiteUsername = null;
        this.blackUsername = null;
    }

    public int id(){
        return id;
    }
    public String name(){
        return name;
    }
    public String whiteUsername(){
        return whiteUsername;
    }
    public String blackUsername(){
        return blackUsername;
    }

    public void setWhiteUsername(String whiteUsername){
        this.whiteUsername = whiteUsername;
    }
    public void setBlackUsername(String blackUsername){
        this.blackUsername = blackUsername;
    }
}
