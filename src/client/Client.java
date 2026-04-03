package client;

import com.google.gson.Gson;
import shared.Player;
import shared.Vector2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Client implements Runnable{

    private DataInputStream in;
    private DataOutputStream out;

    private volatile Boolean usernameCheckResult = null;
    private volatile Player player;
    private CopyOnWriteArrayList<Player> allPlayers = new CopyOnWriteArrayList<>();

    private GameWindow window;

    boolean running = true;

    public void setWindow(GameWindow window) {
        this.window = window;
    }

    public boolean connect(String ipAddress, int port) {

        try {
            Socket socket = new Socket(ipAddress, port);
            System.out.println("Connecting to server...");

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            out.writeByte(1);
            out.flush();

            new Thread(this).start();

            return true;

        } catch (IOException e) {
            System.err.println("Cannot establish connection with server!");
            return false;
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
                        player = new Player(-1, "", new Vector2(0, 0));
                    }
                }
                else if (packetId == 7) {
                    allPlayers = new CopyOnWriteArrayList<>();
                    int size = in.readInt();
                    for (int i = 0; i < size; i++) {
                        allPlayers.add(new Player(in.readInt(), in.readUTF(), new Vector2(in.readInt(), in.readInt())));
                    }

                    if (this.window != null) {
                        this.window.repaint();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Disconnected from server!");
        } catch (Exception e) {
            System.out.println("Crash!");
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
            if (player.getId() != -1) {
                out.writeByte(5);
                out.flush();
            }
            return player;
        } catch (Exception e) {
            System.err.println("Failed to send login request!");
            return new Player(-1, "", new Vector2(0, 0));
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
            }
        } catch (IOException e) {
            System.err.println("ERROR! Message not sent!");
        }
    }

    public void quit() {
        try {
            out.writeByte(0);
            out.flush();
            in.close();
            out.close();
            running = false;
        } catch (IOException e) {
            System.err.println("Generic error");
        }
    }

    //PACKET 7 - Client Send Movement
    public void sendMovement(boolean sprint, char key) {
        try {
            out.writeByte(7);
            out.writeBoolean(sprint);
            out.writeChar(key);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public CopyOnWriteArrayList<Player> getAllPlayers() {
        return allPlayers;
    }
}
