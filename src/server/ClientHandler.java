package server;

import com.google.gson.Gson;
import shared.Player;
import shared.Vector2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable{

    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    Player player;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte packetId = in.readByte();


                if (packetId == 0) {
                    Server.kill(this);
                }
                else if (packetId == 1) {
                    out.writeByte(1);
                    out.flush();
                }
                else if (packetId == 2) {
                    String username = in.readUTF();
                    out.writeByte(2);
                    out.writeBoolean(Server.checkUsername(username));
                    out.flush();
                }

                else if (packetId == 3) {
                    String username = in.readUTF();
                    String password = in.readUTF();
                    String data = "";

                    if (Server.checkUsername(username)) {
                        data = Server.loadPlayer(username, password);
                    }

                    synchronized (out) {
                        out.writeByte(4);
                        out.writeUTF(data);
                        out.flush();
                    }

                    Gson gson = new Gson();
                    if (!data.isEmpty()){
                        player = gson.fromJson(data, Player.class);
                    }
                }

                else if (packetId == 4) {
                    String username = in.readUTF();
                    String password = in.readUTF();
                    if (!Server.checkUsername(username)) {
                        Server.createAccount(username, password);
                    }
                }

                else if (packetId == 5) {
                    System.out.println(player.getUsername() + " joined the server!");
                }

                else if (packetId == 6) {
                    String message = in.readUTF();
                    Server.broadcastMessage("<" + player.getUsername() + "> " + message);
                }

                else if (packetId == 7) {
                    boolean sprint = in.readBoolean();
                    char key = in.readChar();

                    updateMovement(sprint, key);
                }
            }
        } catch (IOException e) {
            if (player != null) {
                System.out.println(player.getUsername() + " left the server");
            } else {
                System.out.println("An unregistered client disconnected.");
            }
            Server.kill(this);
        }
    }

    public void updateMovement(boolean sprint, char key) {
        Vector2 vector = Vector2.vecEmpty;
        switch (key) {
            case 's': vector = Vector2.vecDown; break;
            case 'w': vector = Vector2.vecUp; break;
            case 'a': vector = Vector2.vecLeft; break;
            case 'd': vector = Vector2.vecRight; break;
        }
        if (sprint) {
            player.changePos(vector.vecMul(Server.sprintMul));
        } else {
            player.changePos(vector);
        }
        System.out.println("New playerpos = " + player.getPos().x + " " + player.getPos().y);
    }

    public void sendMessageToClient(String message) {
        try {
            if (out != null) {
                out.writeByte(6);
                out.writeUTF(message);
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Failed to send message to " + player.getUsername());
            Server.kill(this);
        }
    }

    public void updatePlayers(CopyOnWriteArrayList<Player> allPlayers) {
        try {
            synchronized (out){
                out.writeByte(7);
                out.writeInt(allPlayers.size());
                for (Player player : allPlayers) {
                    out.writeInt(player.getId());
                    out.writeUTF(player.getUsername());
                    out.flush();
                    out.writeInt(player.getPos().x);
                    out.writeInt(player.getPos().y);
                }
            }
            out.flush();
        } catch (IOException e) {
            System.err.println("Failed to share players information");
        }
    }
}
