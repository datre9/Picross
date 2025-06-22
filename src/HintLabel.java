import javax.swing.*;
import java.awt.*;

public class HintLabel extends JLabel {
    public HintLabel() {
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        setFont(new Font("Arial", Font.BOLD, 15));
    }
}