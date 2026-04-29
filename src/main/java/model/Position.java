package model;

import java.util.Objects;
/**
 * Represents a position on the board using row and column coordinates.
 * This class is immutable so positions can be safely passed around
 * without being changed accidentally.
 */
public class Position {
    private final int row;
    private final int col;

    /**
     * Creates a position with the given row and column.
     *
     * @param row the row index
     * @param col the column index
     */
    public Position(int row, int col){
        this.row = row;
        this.col = col;
    }

    public int getRow(){ return row;}
    public int getCol(){ return col;}

    /**
     * Checks whether two Position objects represent the same coordinates.
     *
     * @param o the object to compare
     * @return true if both positions have the same row and column
     */
    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (!(o instanceof Position))
            return false;
        Position other = (Position) o;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode(){
        return Objects.hash(row, col);
    }

    @Override
    public String toString(){
        return "(" + row + ", " + col + ")";
    }
}
