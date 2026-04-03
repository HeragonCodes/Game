package client;

import shared.Player;

import java.util.Scanner;

public class Main {

    static void main() {

        System.out.println("Game is starting up...");

        Client client = new Client();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Connecting to server...");

        System.out.print("IP: ");

        String ip = scanner.nextLine();

        if (ip.isEmpty()) {
            ip = "localhost";
        }

        if (!client.connect(ip, 8080)) {
            System.exit(1);
        }

        System.out.println("Connection established.");

        Player player = null;
        boolean loginSuccess = false;

        while (!loginSuccess) {

            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.println("Checking for username validity...");
            boolean accountExists = client.attemptUsername(username);

            if (accountExists) {
                System.out.print("Account found, please enter the password: ");
                while (true) {
                    String password = scanner.nextLine();
                    player = client.tryLogin(username, password);
                    if (player.getId() != -1) {
                        break;
                    }
                    System.err.print("The password is wrong... try again: ");
                }
                loginSuccess = true;

            } else {
                System.out.println("No account found, would you like to create a new one? (Y for yes)");
                if (scanner.nextLine().equalsIgnoreCase("y")) {
                    System.out.println("Choose a password: ");
                    String password = scanner.nextLine();
                    client.createNewAccount(username, password);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    player = client.tryLogin(username, password);
                    loginSuccess = true;
                }
            }
        }

        if (player != null){
            System.out.println(player.getUsername() + " was loaded!");
        } else {
            System.err.println("Player not loaded!");
        }

        assert player != null;

        System.out.println("Entered the server as " + player.getUsername());

        GameWindow window = new GameWindow(client);
        client.setWindow(window);
        window.startGraphics();

        boolean running = true;

            while (running) {

                System.out.print(">");
                String command = scanner.nextLine();

                if (command.equals("quit")) {
                    running = false;
                    client.quit();
                } else if (command.equals("msg")) {
                    String message = scanner.nextLine();
                    client.sendMessage(message);
                } else if (command.equals("mv")) {
                    String input = scanner.nextLine();
                    char key = input.toCharArray()[0];
                    boolean sprint = false;
                    if (Character.isUpperCase(key)) {
                        sprint = true;
                    }
                    key = Character.toLowerCase(key);
                    client.sendMovement(sprint, key);
                }
            }
    }
}
