package actor;

import java.util.ArrayDeque;
import java.util.Deque;

import model.Board;
import model.Position;

/**
 * Player controlled character.
 */
public class MainCharacter extends Actor {

    private final Deque<Position> pathHistory = new ArrayDeque<>();
    private int maxHistorySize = 10; // Default history size
    private int trapInventory = 0; // The number of traps the player has

    /**
     * Creates a main character at the given position on the given board.
     * 
     * @param startPos starting position of the main character
     * @param board the board the main character is on
     */
    public MainCharacter(Position startPos, Board board) {
        super(startPos, board);
        recordStep(startPos);
    }

    /**
     * Applies the given directional input for this turn.
     * 
     * @param dir movement direction
     */
    public void handleInput(Direction dir) {
        if (tryMove(dir)) {
            recordStep(getPosition());
        }
    }

    /**
     * Records a step in the path history.
     * 
     * @param pos the position to record
     */
    private void recordStep(Position pos) {
        if (pos == null) return;

        pathHistory.addLast(pos);
        
        while (pathHistory.size() > maxHistorySize) {
            pathHistory.removeFirst();
        }
    }

    /**
     * @return path history
     */
    public Deque<Position> getPathHistory() {
        return new ArrayDeque<>(pathHistory);
    }

    /**
     * Updates the maximum history size
     */
    public void setMaxHistorySize(int maxHistorySize) {
        if (maxHistorySize < 1) throw new IllegalArgumentException("Max history size must be at least 1");
        this.maxHistorySize = maxHistorySize;
        while (pathHistory.size() > maxHistorySize) {
            pathHistory.removeFirst();
        }
    }

    /**
     * Adds to the number of traps the player has after pickup
     */
    public void addTrap() { trapInventory++; }

    /**
     * Decreases the number of traps the player has after use
     */
    public boolean useTrap() {
        if (trapInventory <= 0) return false;
        trapInventory--;
        return true;
    }

    /**
     * A getter for the number of traps in inventory
     */
    public int getTrapInventory() {
        return trapInventory;
    }
}


