package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener implements Runnable{

    private final int port;

    public ConnectionListener(int port) {
        this.port = port;
    }

    @Override
    public void run() {

        System.out.println("Ready to accept client requests.");

        while (true) {

            try (ServerSocket serverSocket = new ServerSocket(port)){

                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                Server.connectedClients.add(clientHandler);
                new Thread(clientHandler).start();

            } catch (IOException e) {
                System.err.println("ERROR! Cannot accept client request.");
            }
        }
    }
}
