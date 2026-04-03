package shared;

import java.util.Vector;

public class Player {

    private final int id;
    private String username;
    private Vector2 pos;

    public Player(int id, String username, Vector2 pos){
        this.id = id;
        this.username = username;
        this.pos = pos;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Vector2 getPos() {return pos;}

    public void changePos(Vector2 vector) {pos = pos.vecAdd(vector);}

    public void changeName(String newName) {
        username = newName;
    }

}
