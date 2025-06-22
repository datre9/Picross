import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    JMenuItem playItem;

    int rows;
    int cols;

    int seed;
    Picross picross;

    int errors = 0;
    int minesFound = 0;

    public MyFrame() {
        setTitle("Picross Game");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize panels
        centerPanel = new JPanel();
        northPanel = new JPanel();
        westPanel = new JPanel();

        centerLayout = new GridLayout(1, 1);
        northLayout = new GridLayout(1, 1);
        westLayout = new GridLayout(1, 1);

        centerPanel.setLayout(centerLayout);
        northPanel.setLayout(northLayout);
        westPanel.setLayout(westLayout);

        centerPanel.setBackground(Color.BLACK);
        northPanel.setBackground(Color.BLACK);
        westPanel.setBackground(Color.BLACK);

        // Layout container with GridBagLayout
        JPanel layoutPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Top-left corner: use a black panel instead of rigid area
        JPanel topLeftCorner = new JPanel();
        topLeftCorner.setBackground(Color.BLACK);
        topLeftCorner.setPreferredSize(new Dimension(50, 50));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        layoutPanel.add(topLeftCorner, gbc);

        // North (column hints)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        gbc.weighty = 0.1;
        layoutPanel.add(northPanel, gbc);

        // West (row hints)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.weighty = 0.9;
        layoutPanel.add(westPanel, gbc);

        // Center (game grid)
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.9;
        gbc.weighty = 0.9;
        layoutPanel.add(centerPanel, gbc);

        add(layoutPanel, BorderLayout.CENTER);

        setupMenu();
        setVisible(true);
    }

    private void setupMenu() {
        menuBar = new JMenuBar();
        playMenu = new JMenu("Play");

        playItem = new JMenuItem("Start Game");
        playItem.addActionListener(this);

        playMenu.add(playItem);

        menuBar.add(playMenu);

        setJMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == playItem) {
            // User input for game parameters
            getGameParameters();

            // Create new Picross game from user parameters
            picross = new  Picross(rows, cols, seed);

            // Create new game grid from parameters
            createGrid();

            // Reset game state
            minesFound = 0;
            errors = 0;

            // Add buttons to center panel
            // TODO: Button doesnt change foreground (text) color on click
            for (int i = 0; i < rows * cols; i++) {
                JButton button = new JButton();
                button.setName("" + i);
                button.setBackground(Color.BLACK);
                button.setOpaque(true);
                button.setBorderPainted(true);
                button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                button.setFont(new Font("Arial", Font.BOLD, 20));
                button.setFocusable(false);
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (!button.isEnabled()) {
                            return;  // Ignore clicks on disabled buttons
                        }

                        int index = Integer.parseInt(e.getComponent().getName());

                        // TODO: Reformat, left/right only changes boolean, switch after that
                        if (e.getButton() == MouseEvent.BUTTON1) {  // Left click
                            int isMine = calculateHit(index, true);

                            switch (isMine) {
                                case -1:
                                    button.setBackground(Color.DARK_GRAY);
                                    button.setForeground(Color.RED);
                                    button.setText("X");
                                    errors++;
                                    break;
                                case 2:
                                    button.setBackground(Color.BLUE);
                                    minesFound++;
                                    break;
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {  // Right click
                            int isMine = calculateHit(index, false);

                            switch (isMine) {
                                case -2:
                                    button.setBackground(Color.BLUE);
                                    button.setForeground(Color.RED);
                                    button.setText("X");
                                    errors++;
                                    minesFound++;
                                    break;
                                case 1:
                                    button.setBackground(Color.DARK_GRAY);
                                    break;
                            }
                        }

                        // Check if the game is won
                        if (minesFound == picross.getMinesTotal()) {
                            JOptionPane.showMessageDialog(MyFrame.this,
                                    "Congratulations! You found all mines! \n" +
                                            "You made this many mistakes: " + errors + "\n"
                                            + "Seed: " + seed + ", Board Size: " + rows + "x" + cols);
                        }

                        button.repaint();
                        button.setEnabled(false);  // Disable button after click
                    }
                });
                centerPanel.add(button);
            }

            // Create hint labels for game
            createHints();

            // Redraw the window
            revalidate();
            repaint();
        }
    }

    private void createGrid() {
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
    }

    private void getGameParameters() {
        String line = JOptionPane.showInputDialog(this,
                "Enter the number of rows, columns, and seed or nothing for random seed (space-separated):");

        String[] parts = line.split(" ");
        rows = Integer.parseInt(parts[0]);
        cols = Integer.parseInt(parts[1]);
        if (parts.length == 2) {
            seed = rand.nextInt();
        } else {
            seed = Integer.parseInt(parts[2]);
        }
    }

    private void createHints() {
        // Add labels to north panel
        for (int i = 0; i < cols; i++) {
            HintLabel label = new HintLabel();

            StringBuilder text = new StringBuilder("<html><center>");
            var hint = picross.getMinesInCols()[i];
            for (Integer integer : hint) {
                text.append(integer).append("<br>");
            }
            text.append("</center></html>");

            label.setText(text.toString());
            northPanel.add(label);
        }

        // Add labels to west panel
        for (int i = 0; i < rows; i++) {
            HintLabel label = new HintLabel();

            StringBuilder text = new StringBuilder();
            var hint = picross.getMinesInRows()[i];
            for (Integer integer : hint) {
                text.append(integer).append(" ");
            }

            label.setText(text.toString());
            westPanel.add(label);
        }
    }

    private int calculateHit(int index, boolean hitIsMine) {
        int row = index / cols;
        int col = index % cols;

        return picross.hitCell(row, col, hitIsMine);
    }
}