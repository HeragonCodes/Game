package client;

import com.google.gson.Gson;
import shared.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable{

    private DataInputStream in;
    private DataOutputStream out;

    private volatile Boolean loginResult = null;
    private Player player;
    boolean running = true;

    public void connect(String ipAddress, int port) {

        try (Socket socket = new Socket(ipAddress, port)){
            System.out.println("Connecting to server...");

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            System.out.println("Connected successfully!");

            new Thread(this).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        try {
            if (!message.equals("quit")) {
                out.writeByte(2);
                out.writeUTF(message);
            } else {
                running = false;
            }
        } catch (IOException e) {
            System.out.println("ERROR! Message not sent!");
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                byte packetId = in.readByte();
                if (packetId == 0) {
                    loginResult = in.readBoolean();
                }
                if (packetId == 2) {
                    System.out.println(in.readUTF());
                }
                if (packetId == 4) {
                    Gson gson = new Gson();
                    String data = in.readUTF();
                    if (!data.isEmpty()) {
                        player = gson.fromJson(data, Player.class);
                    } else {
                        player = new Player(-1, "");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server");
        }
    }

    public boolean attemptUsername(String username) {
        try {
            loginResult = null;
            out.writeByte(0);
            out.writeUTF(username);
            out.flush();
            while (loginResult == null) {
                Thread.sleep(50);
            }

            return loginResult;

        } catch (Exception e) {
            System.out.println("Error communicating with server.");
            return false;
        }
    }

    public Player tryLogin(String username, String password) {
        try {
            player = null;
            out.writeByte(4);
            out.writeUTF(username);
            out.writeUTF(password);
            out.flush();
            while (player == null) {
                Thread.sleep(50);
            }
            return player;
        } catch (Exception e) {
            System.out.println("Failed to send login request!");
            return new Player(-1, "");
        }
    }

    public void createNewAccount(String username, String password) {
        try {
            out.writeByte(3);
            out.writeUTF(username);
            out.writeUTF(password);
            out.flush();
        } catch (IOException e) {
            System.out.println("An error occurred while trying to setup your new account!");
        }

    }
}
