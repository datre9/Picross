import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GridButton extends JButton {
    private final IntegerWrapper minesFound;
    private final IntegerWrapper errors;
    private final Picross picross;
    private final int rows;
    private final int cols;
    private final int seed;

    public GridButton(Picross picross, int rows, int cols, int index, IntegerWrapper minesFound, IntegerWrapper errors, int seed) {
        this.picross = picross;
        this.rows = rows;
        this.cols = cols;
        this.minesFound = minesFound;
        this.errors = errors;
        this.seed = seed;
        setName("" + index);

        setBackground(Color.BLACK);
        setOpaque(true);
        setBorderPainted(true);
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        setFont(new Font("Arial", Font.BOLD, 20));
        setFocusable(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                click(e);
            }
        });
    }

    private void click(MouseEvent e) {
        // Ignore clicks on disabled buttons
        if (!isEnabled()) {
            return;
        }

        // Get the index of the button from its name
        int index = Integer.parseInt(e.getComponent().getName());

        boolean isLeftClick;

        // Left click
        if (e.getButton() == MouseEvent.BUTTON1) {
            isLeftClick = true;
            // Right click
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            isLeftClick = false;
        } else {
            // Ignore other mouse buttons
            return;
        }

        // Check the cell
        int isMine = calculateHit(index, isLeftClick);

        // Update button based on hit result
        switch (isMine) {
            case -2:
                setBackground(Color.BLUE);
                setForeground(Color.RED);
                setText("X");
                errors.increment();
                minesFound.increment();
                break;
            case -1:
                setBackground(Color.DARK_GRAY);
                setForeground(Color.RED);
                setText("X");
                errors.increment();
                break;
            case 1:
                setBackground(Color.DARK_GRAY);
                break;
            case 2:
                setBackground(Color.BLUE);
                minesFound.increment();
                break;
        }

        // Check if the game is won
        checkGameWon();

        //Redraw and disable the button
        repaint();
        setEnabled(false);
    }

    private void checkGameWon() {
        if (minesFound.getValue() == picross.getMinesTotal()) {
            JOptionPane.showMessageDialog(getParent(),
                    "Congratulations! You found all mines! \n" +
                            "You made this many mistakes: " + errors.getValue() + "\n"
                            + "Seed: " + seed + ", Board Size: " + rows + "x" + cols);
        }
    }

    // Calculates the coordinates and performs the hit
    private int calculateHit(int index, boolean hitIsMine) {
        int row = index / cols;
        int col = index % cols;

        return picross.hitCell(row, col, hitIsMine);
    }
}