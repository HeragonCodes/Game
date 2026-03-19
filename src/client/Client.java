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

    private volatile Boolean usernameCheckResult = null;
    private volatile Player player;

    boolean running = true;



    public void connect(String ipAddress, int port) {

        try {
            Socket socket = new Socket(ipAddress, port);
            System.out.println("Connecting to server...");

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            out.writeByte(1);
            out.flush();

            new Thread(this).start();
        } catch (IOException e) {
            System.err.println("Cannot establish connection with server!");
        }
    }



    @Override
    public void run() {
        try {
            while (running) {

                byte packetId = in.readByte();

                if (packetId == 0) {
                    System.err.println("Server closed!");
                    in.close();
                    out.close();
                    running = false;
                }
                else if (packetId == 1) {
                    System.out.println("Connection established.");
                }
                else if (packetId == 2) {
                    usernameCheckResult = in.readBoolean();
                }
                else if (packetId == 3) {

                }
                else if (packetId == 6) {
                    System.out.println(in.readUTF());
                }
                else if (packetId == 4) {
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
            System.err.println("Disconnected from server!");
        }
    }


    // PACKET 2 - Client Check If Username Exists
    public boolean attemptUsername(String username) {
        try {
            usernameCheckResult = null;
            out.writeByte(2);
            out.writeUTF(username);
            out.flush();
            while (usernameCheckResult == null) {
                Thread.sleep(50);
            }
            return usernameCheckResult;

        } catch (Exception e) {
            System.err.println("Error communicating with server.");
            return false;
        }
    }


    // PACKET 3 - Client Attempt Login
    public Player tryLogin(String username, String password) {
        try {
            player = null;
            out.writeByte(3);
            out.writeUTF(username);
            out.writeUTF(password);
            out.flush();
            while (player == null) {
                Thread.sleep(50);
            }
            out.writeByte(5);
            out.flush();
            return player;
        } catch (Exception e) {
            System.err.println("Failed to send login request!");
            return new Player(-1, "");
        }
    }


    // PACKET 4 - Client Attempt Account Creation
    public void createNewAccount(String username, String password) {
        try {
            out.writeByte(4);
            out.writeUTF(username);
            out.writeUTF(password);
            out.flush();
        } catch (IOException e) {
            System.err.println("An error occurred while trying to setup your new account!");
        }

    }


    // PACKET 6 - Client Send Message || PACKET 0 - Quit
    public void sendMessage(String message) {
        try {
            if (!message.equals("quit")) {
                out.writeByte(6);
                out.writeUTF(message);
                out.flush();
            } else {
                out.writeByte(0);
                out.flush();
                in.close();
                out.close();
                running = false;
            }
        } catch (IOException e) {
            System.err.println("ERROR! Message not sent!");
        }
    }


}
