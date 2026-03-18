package client;

import shared.Player;

import java.util.Scanner;

public class Main {

    static void main() {

        System.out.println("Game is starting up...");

        Client client = new Client();
        client.connect("localhost", 8080);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");
        String username = scanner.nextLine();
        boolean accountExists = client.attemptUsername(username);

        Player player = null;

        if (accountExists) {
            System.out.println("Account found, please enter the password: ");
            String password = scanner.nextLine();
            player = client.tryLogin(username, password);
        } else {
            System.out.println("No account found, would you like to create a new one? (Y for yes)");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.println("Choose a password: ");
                String password = scanner.nextLine();
                client.createNewAccount(username, password);
                player = client.tryLogin(username, password);
            }
        }

        if (player != null){
            System.out.println(player.getUsername() + " was loaded!");
        } else {
            System.out.println("Player not loaded!");
        }

        boolean running = true;

            while (running) {
                String message = scanner.nextLine();
                if (message.equals("quit")) {
                    running = false;
                }
                client.sendMessage(message);
            }
    }
}
