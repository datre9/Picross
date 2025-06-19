import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Picross {
    // indicates whether the solution's a cell contains a mine (true) or not (false)
    private final boolean[][] realGrid;
    // stores the gird to be displayed to the player
    // -2 indicates an error that is a mine ,-1 indicates an error that is not a mine
    // 0 is an unmarked cell
    // 1 is a marked cell that is not a mine, 2 is a marked cell that is a mine
    private final int[][] playerGrid;

    private ArrayList<Integer>[] minesInRows;
    private ArrayList<Integer>[] minesInCols;

    private int longestRow;
    private int longestCol;

    private int minesTotal = 0;
    private int errors = 0;

    Scanner input = new Scanner(System.in);

    public Picross(int rows, int cols, int seed) {
        realGrid = new boolean[rows][cols];
        minesInRows = new ArrayList[rows];
        minesInCols = new ArrayList[cols];

        Random rand = new Random(seed);

        // randomly generates mines using the seed
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                realGrid[row][col] = rand.nextBoolean();
                if (realGrid[row][col]) {
                    minesTotal++;
                }
            }
        }

        // calculate the hints
        countMines();

        // initialize the player grid with unmarked cells
        playerGrid = new int[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                playerGrid[row][col] = 0;
            }
        }

        // initiate game
        //game();
    }

    // game loop
    public void game() {
        while (true) {
            System.out.println(this);

            // check if there are unmarked cells
            // check number of mines found (right and wrong)
            int foundMines = 0;
            boolean gameOver = false;
            for (int[] ints : playerGrid) {
                for (int anInt : ints) {
                    if (anInt == 0) {
                        gameOver = true;
                    } else if (anInt == 2 || anInt == -2) {
                        foundMines++;
                    }
                }
            }

            // if there aren't
            // or if the player has found all mines
            // end the game
            if (!gameOver || foundMines == minesTotal) {
                System.out.println("Game over! You made this many errors: " + errors);
                break;
            }

            // get user input
            System.out.print("Enter row and column (0-indexed) and whether you think it's a mine (true/false), separated by spaces: ");
            String line = input.nextLine();
            String[] parts = line.split(" ");
            if (parts.length != 3) {
                System.out.println("Invalid input. Please enter row, column, and hitIsMine (true/false).");
                continue;
            }

            // parse the input and hit the cell
            try {
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                boolean hitIsMine = parts[2].equals("true") || parts[2].equals("t") || parts[2].equals("1");

                hitCell(row, col, hitIsMine);

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter valid integers for row and column.");
            } catch (IndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    // marks a cell as hit by the player.
    // hitIsMine indicates player action:
    // true if the player thinks the cell is a mine, false if they think it is not a mine
    public int hitCell(int row, int col, boolean hitIsMine) {
        // if the cell is already marked, do nothing
        if (playerGrid[row][col] != 0) {
            System.out.println("Cell (" + row + ", " + col + ") is already marked. Please choose another cell.");
            return 0;
        }

        if (!hitIsMine && realGrid[row][col]) {
            // player: is not a mine, but the cell is a mine
            errors++;
            return -2;
        } else if (hitIsMine && !realGrid[row][col]) {
            // player: is a mine, but the cell is not a mine
            errors++;
            return -1;
        } else if (!hitIsMine && !realGrid[row][col]) {
            // player: is not a mine, and the cell is not a mine
            return 1;
        } else if (hitIsMine && realGrid[row][col]) {
            // player: is a mine, and the cell is a mine
            return 2;
        }

        return 0;
    }

    private void countMines() {
        // count mines in each row
        minesInRows = countAllRows(realGrid);

        // count mines in each column using transposition
        boolean[][] transposedRealGrid = transpose(realGrid);
        minesInCols = countAllRows(transposedRealGrid);

        // find the longest row and column
        longestRow = 0;
        longestCol = 0;
        for (ArrayList<Integer> row : minesInRows) {
            if (row.size() > longestRow) {
                longestRow = row.size();
            }
        }
        for (ArrayList<Integer> col : minesInCols) {
            if (col.size() > longestCol) {
                longestCol = col.size();
            }
        }
    }

    private ArrayList<Integer>[] countAllRows(boolean[][] Grid) {
        ArrayList<Integer>[] minesInRows = new ArrayList[Grid.length];

        // split the problem into rows
        for (int row = 0; row < Grid.length; row++) {
            minesInRows[row] = countInRow(Grid[row]);
        }

        return minesInRows;
    }

    private ArrayList<Integer> countInRow(boolean[] booleans) {
        ArrayList<Integer> minesInRow = new ArrayList<>();
        int count = 0;

        // count 'pockets' of mines
        for (boolean aBoolean : booleans) {
            if (aBoolean) {
                count++;
            } else {
                minesInRow.add(count);
                count = 0;
            }
        }
        minesInRow.add(count);

        // remove unnecessary zeros
        minesInRow.removeAll(Collections.singleton(0));

        return minesInRow;
    }

    private boolean[][] transpose(boolean[][] grid) {
        boolean[][] transposedGrid = new boolean[grid[0].length][grid.length];
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                transposedGrid[col][row] = grid[row][col];
            }
        }
        return transposedGrid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // print the top header
        for (int i = 0; i < longestCol; i++) {

            // offset from the left
            sb.append("  ".repeat(Math.max(0, longestRow)));
            sb.append("|");

            // print the counts in columns
            for (ArrayList<Integer> minesInCol : minesInCols) {
                if (minesInCol.size() > i) {
                    sb.append(minesInCol.get(i)).append(" ");
                } else {
                    sb.append("  ");
                }
            }
            sb.append('\n');
        }

        // print the separator line
        for (int i = 0; i < minesInCols.length + longestRow; i++) {
            if (i != longestRow) {
                sb.append("__");
            } else {
                sb.append("|_");
            }
        }
        sb.append('\n');

        // print the grid with the left header
        // debug display of the grid
        //        for (int i = 0; i < realGrid.length; i++) {
        //            boolean[] booleans = realGrid[i];
        //
        //            // print the counts in rows
        //            for (int j = 0; j < longestRow; j++) {
        //                if (minesInRows[i].size() > j) {
        //                    sb.append(minesInRows[i].get(j)).append(" ");
        //                } else {
        //                    sb.append("  ");
        //                }
        //            }
        //            sb.append("|");
        //
        //            // print the actual grid
        //            for (boolean aBoolean : booleans) {
        //                if (aBoolean) {
        //                    sb.append('X');
        //                } else {
        //                    sb.append('-');
        //                }
        //                sb.append(" ");
        //            }
        //            sb.append('\n');
        //        }

        // print the grid with the left header
        // display the player's grid
        for (int row = 0; row < playerGrid.length; row++) {
            // print the counts in rows
            for (int i = 0; i < longestRow; i++) {
                if (minesInRows[row].size() > i) {
                    sb.append(minesInRows[row].get(i)).append(" ");
                } else {
                    sb.append("  ");
                }
            }
            sb.append("|");

            // print the actual grid
            for (int col = 0; col < playerGrid[row].length; col++) {
                switch (playerGrid[row][col]) {
                    case -2 -> sb.append("M "); // error - marked cell is a mine
                    case -1 -> sb.append("E "); // error - marked cell is not a mine
                    case 0 -> sb.append(". "); // unmarked cell
                    case 1 -> sb.append("- "); // marked cell is not a mine
                    case 2 -> sb.append("X "); // marked cell is a mine
                }
            }
            sb.append('\n');
        }

        return sb.toString();
    }
}