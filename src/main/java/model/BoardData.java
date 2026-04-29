package model;

public class BoardData {
    private final int width;
    private final int height;
    private final Cell[][] cells;
    private final Position startPos;
    private final Position exitPos;

    public BoardData(int width, int height, Cell[][] cells, Position startPos, Position exitPos) {
        if (cells == null || startPos == null || exitPos == null) {
            throw new IllegalArgumentException("BoardData fields cannot be null");
        }
        this.width = width;
        this.height = height;
        this.cells = cells;
        this.startPos = startPos;
        this.exitPos = exitPos;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public Position getStartPos() {
        return startPos;
    }

    public Position getExitPos() {
        return exitPos;
    }
}