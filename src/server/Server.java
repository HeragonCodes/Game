package server;

import shared.Player;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server {

    public static CopyOnWriteArrayList<ClientHandler> connectedClients = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Player> allPlayers = new CopyOnWriteArrayList<>();

    public static int sprintMul = 2;

    public static void main(String[] args) throws IOException {

        int port = 8080;
        new Thread(new ConnectionListener(port)).start();

        while (true){
            gameLoop();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.err.println("GENERIC ERROR!");
            }
        }
    }

    public static void gameLoop() {
        sharePlayers();
    }

    public static void broadcastMessage(String message) {
        for (ClientHandler client : connectedClients) {
            client.sendMessageToClient(message);
        }
    }

    public static void sharePlayers() {
        allPlayers = new CopyOnWriteArrayList<>();
        for (ClientHandler client : connectedClients) {
            if (client.player != null) {
                allPlayers.add(client.player);
            }
        }
        for (ClientHandler client : connectedClients) {
            if (client.player != null) {
                client.updatePlayers(allPlayers);
            }
        }
    }

    public static void kill(ClientHandler clientHandler) {
        connectedClients.remove(clientHandler);
    }

    public static String loadPlayer(String username, String password) {
        return AccountsDatabase.loadPlayer(username, password);
    }

    public static boolean checkUsername(String username) {
        return AccountsDatabase.checkForUsername(username);
    }

    public static void createAccount(String username, String password) throws IOException {
        AccountsDatabase.addAccount(username, password);
        AccountsDatabase.createNewPlayer(AccountsDatabase.loadAccount(username, password), username);
    }

}