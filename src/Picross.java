import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Picross {
    // indicates whether the solution's a cell contains a mine (true) or not (false)
    private final boolean[][] realGrid;

    private ArrayList<Integer>[] minesInRows;
    private ArrayList<Integer>[] minesInCols;

    private int longestRow;
    private int longestCol;
    private int minesTotal = 0;

    public ArrayList<Integer>[] getMinesInRows() {
        return minesInRows;
    }

    public ArrayList<Integer>[] getMinesInCols() {
        return minesInCols;
    }

    public int getMinesTotal() {
        return minesTotal;
    }

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
    }

    // marks a cell as hit by the player.
    // hitIsMine indicates player action:
    // true if the player thinks the cell is a mine, false if they think it is not a mine
    public int hitCell(int row, int col, boolean hitIsMine) {
        if (!hitIsMine && realGrid[row][col]) {
            // player: is not a mine, but the cell is a mine
            return -2;
        } else if (hitIsMine && !realGrid[row][col]) {
            // player: is a mine, but the cell is not a mine
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
                for (int i = 0; i < realGrid.length; i++) {
                    boolean[] booleans = realGrid[i];

                    // print the counts in rows
                    for (int j = 0; j < longestRow; j++) {
                        if (minesInRows[i].size() > j) {
                            sb.append(minesInRows[i].get(j)).append(" ");
                        } else {
                            sb.append("  ");
                        }
                    }
                    sb.append("|");

                    // print the actual grid
                    for (boolean aBoolean : booleans) {
                        if (aBoolean) {
                            sb.append('X');
                        } else {
                            sb.append('-');
                        }
                        sb.append(" ");
                    }
                    sb.append('\n');
                }

        return sb.toString();
    }
}