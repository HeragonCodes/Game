package client;

import shared.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameWindow extends JPanel {

    Client client;

    public GameWindow(Client client) {
        this.client = client;

        this.setFocusable(true);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean sprint = e.isShiftDown();
                char key = ' ';

                if (e.getKeyCode() == KeyEvent.VK_W) key = 'w';
                else if (e.getKeyCode() == KeyEvent.VK_S) key = 's';
                else if (e.getKeyCode() == KeyEvent.VK_D) key = 'd';
                else if (e.getKeyCode() == KeyEvent.VK_A) key = 'a';

                if (key != ' ') {
                    client.sendMovement(sprint, key);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(Color.black);

        CopyOnWriteArrayList<Player> allPlayers = client.getAllPlayers();

        if (!allPlayers.isEmpty()) {

            for (Player p : allPlayers) {

                String username = p.getUsername();
                int posX = p.getPos().x;
                int posY = p.getPos().y;

                //PLAYER SPRITE
                g.setColor(Color.YELLOW);
                g.fillRect(posX, posY, 32, 32);
                //PLAYER USERNAME
                g.setColor(Color.WHITE);
                g.drawString(username, posX, posY-5);

            }

        }

    }

    public void startGraphics() {
        JFrame frame = new JFrame("Game");
        frame.add(this);

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.requestFocusInWindow();
    }
}
