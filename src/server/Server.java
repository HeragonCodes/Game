package server;

import server.ClientHandler;
import shared.Player;
import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server {

    public static CopyOnWriteArrayList<ClientHandler> connectedClients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws IOException {

        try {
            int port = 8080;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                connectedClients.add(clientHandler);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            System.out.println("FATAL ERROR! Cannot start server!");
        }
    }

    public static void broadcastMessage(String message) {
        for (ClientHandler client : connectedClients) {
            client.sendMessageToClient(message);
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
