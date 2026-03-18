package shared;

public class Player {

    private final int id;
    private String username;

    public Player(int id, String username){
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void changeName(String newName) {
        username = newName;
    }

}
