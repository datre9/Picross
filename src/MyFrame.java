import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MyFrame extends JFrame implements ActionListener {
    Random rand = new Random();

    JPanel centerPanel;
    JPanel northPanel;
    JPanel westPanel;
    GridLayout centerLayout;
    GridLayout northLayout;
    GridLayout westLayout;

    JMenuBar menuBar;
    JMenu playMenu;
    JMenu infoMenu;
    JMenuItem playItem;
    JMenuItem errorsItem;

    public MyFrame() {
        setTitle("Picross Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set main layout to BorderLayout
        setLayout(new BorderLayout());

        // Initialize panels with their layouts
        centerPanel = new JPanel();
        northPanel = new JPanel();
        westPanel = new JPanel();

        centerLayout = new GridLayout(1, 1);
        northLayout = new GridLayout(1, 1);
        westLayout = new GridLayout(1, 1);

        centerPanel.setBackground(Color.BLACK);
        northPanel.setBackground(Color.BLACK);
        westPanel.setBackground(Color.BLACK);

        centerPanel.setLayout(centerLayout);
        northPanel.setLayout(northLayout);
        westPanel.setLayout(westLayout);

        // Add panels to frame
        add(centerPanel, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);
        add(westPanel, BorderLayout.WEST);

        // Setup menu
        setupMenu();

        setVisible(true);
    }

    private void setupMenu() {
        menuBar = new JMenuBar();
        playMenu = new JMenu("Play");
        infoMenu = new JMenu("Info");

        playItem = new JMenuItem("Start Game");
        playItem.addActionListener(this);
        errorsItem = new JMenuItem("Errors");
        errorsItem.addActionListener(this);

        playMenu.add(playItem);
        infoMenu.add(errorsItem);

        menuBar.add(playMenu);
        menuBar.add(infoMenu);

        setJMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == playItem) {
            String line = JOptionPane.showInputDialog(this,
                    "Enter the number of rows, columns, and seed or nothing for random seed (space-separated):");

            String[] parts = line.split(" ");
            int rows = Integer.parseInt(parts[0]);
            int cols = Integer.parseInt(parts[1]);
            int seed;
            if (parts.length == 2) {
                seed = rand.nextInt();
            } else {
                seed = Integer.parseInt(parts[2]);
            }

            // Update layouts
            centerLayout.setRows(rows);
            centerLayout.setColumns(cols);
            northLayout.setRows(1);
            northLayout.setColumns(cols);
            westLayout.setRows(rows);
            westLayout.setColumns(1);

            // Clear all panels
            centerPanel.removeAll();
            northPanel.removeAll();
            westPanel.removeAll();

            // Add buttons to center panel
            for (int i = 0; i < rows * cols; i++) {
                JButton button = new JButton();
                button.setBackground(Color.BLACK);
                button.setOpaque(true);
                button.setBorderPainted(true);
                button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                button.setForeground(Color.RED);
                button.setFont(new Font("Arial", Font.BOLD, 15));
                button.setText("X");
                button.setFocusable(false);
                centerPanel.add(button);
            }

            // Add labels to north panel
            for (int i = 0; i < cols; i++) {
                JLabel label = new JLabel("Col " + i, SwingConstants.CENTER);
                createHintLabels(label, northPanel);
            }

            // Add labels to west panel
            for (int i = 0; i < rows; i++) {
                JLabel label = new JLabel("Row " + i, SwingConstants.CENTER);
                createHintLabels(label, westPanel);
            }

            revalidate();
            repaint();
        }
    }

    private void createHintLabels(JLabel label, JPanel westPanel) {
        label.setForeground(Color.WHITE);
        label.setBackground(Color.BLACK);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        label.setFont(new Font("Arial", Font.BOLD, 15));
        westPanel.add(label);
    }
}