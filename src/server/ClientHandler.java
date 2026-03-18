package server;

import com.google.gson.Gson;
import shared.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
                    String username = in.readUTF();
                    out.writeByte(0);
                    out.writeBoolean(Server.checkUsername(username));
                    out.flush();
                }

                else if (packetId == 1) {
                    System.out.println(player.getUsername() + " has joined the server");
                }

                else if (packetId == 2) {
                    String message = in.readUTF();
                    Server.broadcastMessage("<" + player.getUsername() + "> " + message);
                }

                else if (packetId == 3) {
                    String username = in.readUTF();
                    String password = in.readUTF();
                    if (!Server.checkUsername(username)) {
                        Server.createAccount(username, password);
                    }
                }

                else if (packetId == 4) {
                    String username = in.readUTF();
                    String password = in.readUTF();
                    String data = "";
                    if (Server.checkUsername(username)) {
                        data = Server.loadPlayer(username, password);
                        out.writeByte(4);
                        out.writeUTF(data);
                        out.flush();
                    }
                    Gson gson = new Gson();
                    if (!data.isEmpty()){
                        player = gson.fromJson(data, Player.class);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(player.getUsername() + " left the server");
            Server.kill(this);
        }
    }

    public void sendMessageToClient(String message) {
        try {
            if (out != null) {
                out.writeByte(2);
                out.writeUTF(message);
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Failed to send message to " + player.getUsername());
            Server.kill(this);
        }
    }
}
