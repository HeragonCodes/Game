package server;

import com.google.gson.Gson;
import shared.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AccountsDatabase {

    public static boolean checkForUsername(String username) {
        String path = "data\\account\\" + username + ".json";
        File accountFile = new File(path);
        return accountFile.exists();
    }

    public static void addAccount(String username, String password) throws IOException {

        int id;

        try (FileReader reader = new FileReader("data\\currentid.txt")) {
            int i;
            StringBuilder data = new StringBuilder();
            while ((i = reader.read()) != -1) {
                data.append((char) i);
            }

            id = Integer.parseInt(data.toString().trim()) + 1;
        }

        try (FileWriter writer = new FileWriter("data\\currentid.txt")) {
            writer.write(String.valueOf(id));
        }

        Account account = new Account(username, password, id);
        Gson gson = new Gson();

        try (FileWriter fileWriter = new FileWriter("data\\account\\" + username + ".json")) {
            gson.toJson(account, fileWriter);
        }
    }

    public static String loadPlayer(String username, String password) {

        int id = loadAccount(username, password);

        if (id == -1) {
            return "";
        }

        String path = "data\\profile\\" + id + ".json";

        try (FileReader reader = new FileReader(path)) {
            int i;
            StringBuilder data = new StringBuilder();
            while ((i = reader.read()) != -1) {
                data.append((char) i);
            }
            return data.toString();
        } catch (Exception e) {
            System.out.println("Cannot find " + path);
            return "";
        }
    }

    public static void createNewPlayer(int id, String username){
        String path = "data\\profile\\" + id + ".json";

        try (FileWriter writer = new FileWriter(path)) {
            Gson gson = new Gson();
            Player player = new Player(id, username);
            gson.toJson(player, writer);
        } catch (Exception e) {
            System.out.println("Failed to create new Player for " + id);
        }
    }

    public static int loadAccount(String username, String password) {

        String path = "data\\account\\" + username + ".json";

        try (FileReader reader = new FileReader(path)) {

            Gson gson = new Gson();

            Account account = gson.fromJson(reader, Account.class);

            if (password.equals(account.password)) {
                return account.id;
            }

            return -1;

        } catch (IOException e) {
            System.out.println("Could not find " + path);
            return -1;
        }
    }
}
