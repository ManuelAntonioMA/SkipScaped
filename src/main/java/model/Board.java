package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import Item.Item;
import Item.RegularReward;
import Item.BonusReward;

/**
 * Represents the full game board.
 * The board stores all cells, the board dimensions,
 * and the special positions such as the start and exit.
 *
 * It also provides helper methods used by the game logic,
 * such as checking whether a position is inside the board,
 * walkable, or the exit.
 */
public class Board {
    private int width;
    private int height;
    private Cell[][] cells;
    private Position startPos;
    private Position exitPos;

    private final BoardMapLoader mapLoader = new BoardMapLoader();

    public Board() {
        this.width = 10;
        this.height = 10;
        this.cells = new Cell[height][width];

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                Position pos = new Position(r, c);
                cells[r][c] = new Cell(pos, CellType.FLOOR);
            }
        }

        this.startPos = new Position(1, 1);
        this.exitPos = new Position(height - 2, width - 2);

        cells[startPos.getRow()][startPos.getCol()] =
                new Cell(startPos, CellType.START);
        cells[exitPos.getRow()][exitPos.getCol()] =
                new Cell(exitPos, CellType.EXIT);
    }

    public int getWidth(){ return width;}
    public int getHeight(){ return height;}
    public Position getStartPos(){ return startPos;}
    public Position getExitPos(){ return exitPos;}

    public Cell getCell(Position pos){
        if (!isInside(pos))
            throw new IllegalArgumentException("Position out of bounds: " + pos);
        return cells[pos.getRow()][pos.getCol()];
    }

    public boolean isInside(Position pos){
        if (pos == null) {
            return false;
        }
        int r = pos.getRow();
        int c = pos.getCol();
        return r>=0 && r<height && c>=0 && c<width;
    }

    public boolean isWalkable(Position pos){
        if (!isInside(pos))
            return false;
        CellType t = getCell(pos).getTitle();
        return t == CellType.FLOOR || t == CellType.START || t == CellType.EXIT;
    }

    public boolean isExit(Position pos){
        return exitPos != null && exitPos.equals(pos);
    }

    public void removeItem(Position pos){
        getCell(pos).clearItem();
    }

    public int countRegularRemaining(){
        int count = 0;
        if (cells == null)
            return 0;
        for (int r = 0; r<height; r++){
            for (int c = 0; c<width; c++){
                Item it = cells[r][c].getItem();
                if (it != null && (it instanceof RegularReward))
                    count++;
            }
        }
        return count;
    }

    public void placeRegularReward(Position pos, int value) {
        validateRewardPlacement(pos);

        Cell cell = getCell(pos);
        if (cell.hasItem()) {
            throw new IllegalStateException("Cell already has an item at " + pos);
        }

        cell.setItem(new RegularReward(value));
    }

    public void placeBonusReward(Position pos, int value, int spawnTick, int ttl) {
        validateRewardPlacement(pos);

        Cell cell = getCell(pos);
        if (cell.hasItem()) {
            throw new IllegalStateException("Cell already has an item at " + pos);
        }

        cell.setItem(new BonusReward(value, spawnTick, ttl));
    }

    private void validateRewardPlacement(Position pos) {
        if (pos == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }

        if (!isInside(pos)) {
            throw new IllegalArgumentException("Position out of bounds: " + pos);
        }

        if (!isWalkable(pos)) {
            throw new IllegalArgumentException("Rewards must be placed on walkable cells: " + pos);
        }

        if (isExit(pos)) {
            throw new IllegalArgumentException("Cannot place reward on exit: " + pos);
        }

        if (startPos != null && startPos.equals(pos)) {
            throw new IllegalArgumentException("Cannot place reward on start: " + pos);
        }
    }

     /**
     * Loads a map from a text file in the resources/maps folder.
     * The method reads the file, validates its size and shape,
     * and creates the board cells from the characters in the file.
     *
     * @param mapId the map file name without ".txt"
     *
     */
    public void loadFromMap(String mapId) {
        BoardData data = mapLoader.load(mapId);
        applyBoardData(data);
    }

    private void applyBoardData(BoardData data) {
        this.width = data.getWidth();
        this.height = data.getHeight();
        this.cells = data.getCells();
        this.startPos = data.getStartPos();
        this.exitPos = data.getExitPos();
    }
}
