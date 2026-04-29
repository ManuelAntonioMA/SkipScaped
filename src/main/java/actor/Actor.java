package actor;

import model.Board;
import model.Cell;
import model.Position;

/**
 * Base class for any actor in the game (MainCharacter, MovingEnemy).
 */
public abstract class Actor {

    private Position pos;
    private final Board board;

    /**
     * Creates an actor at the given position on the given board.
     * 
     * @param startPos starting position of the actor
     * @param board the board the actor is on
     */

    protected Actor(Position startPos, Board board) {
        if (board == null) throw new IllegalArgumentException("Board cannot be null");
        if (startPos == null) throw new IllegalArgumentException("Position cannot be null");
        if (!board.isInside(startPos)) throw new IllegalArgumentException("Starting position is out of board bounds");
        if (!board.isWalkable(startPos)) throw new IllegalArgumentException("Starting position is not walkable");

        this.pos = startPos;
        this.board = board;

        // Register occupancy of the starting position
        Cell cell = board.getCell(startPos);
        if (cell.hasOccupant()) {
            throw new IllegalArgumentException("Starting position is already occupied by another actor");
        }
        cell.setOccupant(this);
    }   

    /**
     * @return the current position of the actor
     */
    public Position getPosition() {
        return pos;
    }

    /**
     * @return the board the actor is on
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sets the position of the actor to the given position.
     * 
     * @param newPos the new position of the actor
     */
    public void setPosition(Position newPos) {
        if (newPos == null) throw new IllegalArgumentException("Position cannot be null");
        if (!board.isInside(newPos)) throw new IllegalArgumentException("Position is out of board bounds");
        if (!board.isWalkable(newPos)) throw new IllegalArgumentException("Position is not walkable");

        if (newPos.equals(pos)) return; // No movement

        Cell from = board.getCell(pos);
        Cell to = board.getCell(newPos);
        Actor other = to.getOccupant();
        if (other != null && other != this) {
            throw new IllegalArgumentException("Target position is occupied by another actor");
        }

        if (from.getOccupant() == this) {
            from.clearOccupant();
        }
        to.setOccupant(this);
        this.pos = newPos;
    }

    /**
     * Attempts to move this actor by one step in the given direction.
     * 
     * @param dir the direction to move in
     * @return true if the move was successful, false otherwise
     */
    public boolean tryMove(Direction dir) {
        if (dir == null || dir == Direction.NONE) return false;

        Position newPos = nextPosition(pos, dir);
        if (newPos == null) return false;

        if (!canEnter(newPos)) return false;

        Cell from = board.getCell(pos);
        Cell to = board.getCell(newPos);

        if(from.getOccupant() == this) {
            from.clearOccupant();
        }
        to.setOccupant(this);

        // Update the actor's position
        this.pos = newPos;
        return true;
    }

    /**
     * Checks if the actor can enter the cell at the given position.
     * 
     * @param targetPos the position to check
     * @return true if the actor can enter the cell, false otherwise
     */
    protected boolean canEnter(Position targetPos) {
        if (targetPos == null) return false;
        if (!board.isInside(targetPos)) return false;
        if (!board.isWalkable(targetPos)) return false;

        Cell targetCell = board.getCell(targetPos);
        Actor occupant = targetCell.getOccupant();
        return occupant == null || occupant == this;
    }

    /**
     * Helper method to calculate the next position
     * 
     * @param current the current position
     * @param dir the direction to move in
     * @return the next position if the move is valid, null otherwise
     */
    protected static Position nextPosition(Position current, Direction dir) {
        if (current == null || dir == null) return null;

        int r = current.getRow();
        int c = current.getCol();

        if (dir == Direction.UP) return new Position(r-1, c);
        if (dir == Direction.DOWN) return new Position(r+1, c);
        if (dir == Direction.LEFT) return new Position(r, c-1);
        if (dir == Direction.RIGHT) return new Position(r, c+1);
        
        return null;
    }

    /**
     * Directly sets the position of the actor without checking for validity or updating cell occupancy.
     * @param newPos the new position to set
     */
    protected void setPositionDirect(Position newPos) {
        this.pos = newPos;
    }
}
