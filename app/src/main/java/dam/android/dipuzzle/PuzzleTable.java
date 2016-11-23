package dam.android.dipuzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * 0-0 1-0 2-0
 * 0-1 1-1 2-1
 * 0-2 1-2 2-2
 */
class PuzzleTable {

    static final int NO_MOVE = 0;
    static final int UP = 1;
    static final int DOWN = 2;
    static final int LEFT = 3;
    static final int RIGHT = 4;

    private int[][] table;

    PuzzleTable() {
        Random r = new Random();
        do {
            ArrayList<Integer> positions = new ArrayList<>(Arrays.asList(new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8}));
            table = new int[3][];
            for (int i = 0; i < 3; i++) {
                table[i] = new int[3];
                for (int j = 0; j < 3; j++) {
                    table[i][j] = positions.get(r.nextInt(positions.size()));
                    Integer element = Integer.valueOf(table[i][j]);
                    positions.remove((Object) element);
                }
            }
        } while (!isSolvable(order()));
    }

    int getImage(int x, int y) {
        return table[x][y];
    }

    boolean isValidMove(int srcX, int srcY, int move) {
        try {
            switch (move) {
                case UP:
                    return table[srcX][srcY - 1] == 0;
                case DOWN:
                    return table[srcX][srcY + 1] == 0;
                case LEFT:
                    return table[srcX - 1][srcY] == 0;
                case RIGHT:
                    return table[srcX + 1][srcY] == 0;
                default:
                    return false;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    void move(int srcX, int srcY, int move) {
        int org = table[srcX][srcY];
        table[srcX][srcY] = 0;
        switch (move) {
            case UP:
                table[srcX][srcY - 1] = org;
                break;
            case DOWN:
                table[srcX][srcY + 1] = org;
                break;
            case LEFT:
                table[srcX - 1][srcY] = org;
                break;
            case RIGHT:
                table[srcX + 1][srcY] = org;
                break;
        }
    }

    boolean isCompleted() {
        return isSorted(order());
    }

    private boolean isSorted(ArrayList<Integer> array) {
        for (int i=1; i<array.size()-1; i++) {
            if (!(array.get(i-1) < array.get(i))) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<Integer> order() {
        ArrayList<Integer> order = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                order.add(new Integer(table[j][i]));
            }
        }
        return order;
    }

    private boolean isSolvable(ArrayList<Integer> order) {
        int inversions = 0;
        for (int i = 0; i < table.length; i++) {
            for (int j = i+1; j < table.length; j++) {
                if (order.get(j) > order.get(i)) {
                    inversions++;
                }
            }
        }
        return inversions%2 == 0;
    }

    @Override
    public String toString() {
        String ret = "";
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                ret += i + "-" + j + ":" + table[j][i] + " ";
            }
            ret += "\n";
        }
        return ret;
    }
}
