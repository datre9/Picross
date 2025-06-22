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
    JButton playButton;

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
        initializePanels();

        // Layout container with GridBagLayout
        JPanel layoutPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        setUpGridBag(gbc, layoutPanel);

        add(layoutPanel, BorderLayout.CENTER);

        setupMenu();
        setVisible(true);
    }

    private void initializePanels() {
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
    }

    private void setUpGridBag(GridBagConstraints gbc, JPanel layoutPanel) {
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
    }

    private void setupMenu() {
        menuBar = new JMenuBar();
        playButton = new JButton("Start Game");
        playButton.setFocusable(false);
        playButton.addActionListener(this);
        menuBar.add(playButton);
        setJMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == playButton) {
            // User input for game parameters
            getGameParameters();

            // Create new Picross game from user parameters
            picross = new Picross(rows, cols, seed);

            // Create new game grid from parameters
            createGrid();

            // Reset game state
            minesFound = 0;
            errors = 0;

            // Create buttons in grid
            IntegerWrapper minesFoundWrapper = new IntegerWrapper(0);
            IntegerWrapper errorsWrapper = new IntegerWrapper(0);

            for (int i = 0; i < rows * cols; i++) {
                GridButton button = new GridButton(picross, rows, cols, i, minesFoundWrapper, errorsWrapper, seed);
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
        // Prompt user for game parameters
        String line = JOptionPane.showInputDialog(this,
                "Enter the number of rows, columns, and seed or nothing for random seed (space-separated):");

        // Save inputs, seed can be specified or random
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
}