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
    JMenu infoMenu;
    JMenuItem playItem;
    JMenuItem errorsItem;

    int rows;
    int cols;

    Picross picross;

    public MyFrame() {
        setTitle("Picross Game");
        setSize(900, 900);
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
            rows = Integer.parseInt(parts[0]);
            cols = Integer.parseInt(parts[1]);
            int seed;
            if (parts.length == 2) {
                seed = rand.nextInt();
            } else {
                seed = Integer.parseInt(parts[2]);
            }

            picross = new  Picross(rows, cols, seed);

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
                                    break;
                                case 2:
                                    button.setBackground(Color.BLUE);
                                    break;
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {  // Right click
                            int isMine = calculateHit(index, false);

                            switch (isMine) {
                                case -2:
                                    button.setBackground(Color.BLUE);
                                    button.setForeground(Color.RED);
                                    button.setText("X");
                                    break;
                                case 1:
                                    button.setBackground(Color.DARK_GRAY);
                                    break;
                            }
                        }

                        button.repaint();
                        button.setEnabled(false);  // Disable button after click
                    }
                });
                centerPanel.add(button);
            }

            // Add labels to north panel
            for (int i = 0; i < cols; i++) {
                JLabel label = new JLabel("", SwingConstants.CENTER);
                StringBuilder text = new StringBuilder("<html><center>");
                var hint = picross.getMinesInCols()[i];
                for (Integer integer : hint) {
                    text.append(integer).append("<br>");
                }
                text.append("</center></html>");
                label.setText(text.toString());
                createHintLabels(label, northPanel);
            }

            // Add labels to west panel
            for (int i = 0; i < rows; i++) {
                JLabel label = new JLabel("", SwingConstants.CENTER);
                StringBuilder text = new StringBuilder();
                var hint = picross.getMinesInRows()[i];
                for (Integer integer : hint) {
                    text.append(integer).append(" ");
                }
                label.setText(text.toString());
                createHintLabels(label, westPanel);
            }

            revalidate();
            repaint();
        }
    }

    private int calculateHit(int index, boolean hitIsMine) {
        int row = index / cols;
        int col = index % cols;

        return picross.hitCell(row, col, hitIsMine);
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