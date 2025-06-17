import javax.swing.*;
import java.awt.*;

public class MyFrame extends JFrame {

    public MyFrame() {
        setTitle("Picross Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Color.BLACK);


        // Add components here, e.g., a panel for the game
        // JPanel gamePanel = new JPanel();
        // add(gamePanel);

        setVisible(true);
    }
}