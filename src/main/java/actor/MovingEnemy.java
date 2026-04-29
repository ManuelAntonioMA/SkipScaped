package actor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Board;
import model.Cell;
import model.Position;

/**
 * Moving enemy that chases the main character.
 */
public class MovingEnemy extends Actor{

    private int speedFactor;
    private int ticksSinceLastMove;

    /**
     * Creates a moving enemy at the given position on the given board.
     * 
     * @param startPos starting position of the moving enemy
     * @param board the board the moving enemy is on
     * @param speedFactor movement frequency
     */
    public MovingEnemy(Position startPos, Board board, int speedFactor) {
        super(startPos, board);
        if (speedFactor < 1) throw new IllegalArgumentException("Speed factor must be >= 1");
        this.speedFactor = speedFactor;
        this.ticksSinceLastMove = 0;
    }

    /**
     * @return the speed factor of this enemy
     */
    public int getSpeedFactor() {
        return speedFactor;
    }

    /**
     * Updates the speed factor of this enemy.
     * 
     * @param speedFactor the new speed factor (must be >= 1)
     */
    public void setSpeedFactor(int speedFactor) {
        if (speedFactor < 1) throw new IllegalArgumentException("Speed factor must be >= 1");
        this.speedFactor = speedFactor;
    }

    /**
     * update this enemy for the current tick.
     * 
     * @param mainChar the main character to chase
     */
    public void update(MainCharacter mainChar) {
        ticksSinceLastMove++;

        if(!canMoveThisTick()) return;
        ticksSinceLastMove = 0;

        Position next = getNextPositionToFollow(mainChar);
        if (next == null) return;

        moveTo(next);   
    }

    /**
     * Checks whether this enemy can move on the current tick based on its speed factor.
     * 
     * @return true if the enemy can move this tick, false otherwise
     */
    private boolean canMoveThisTick() {
        return ticksSinceLastMove >= speedFactor;
    }

    /**
     * Choose the next position to move towards the main character.
     * 
     * @param mainChar the main character to follow
     * @return the next position to move to, or null if no valid move is available
     */
    private Position getNextPositionToFollow(MainCharacter mainChar) {
        if (mainChar == null) return null;
        else {
            Position start = getPosition();
            Position target = mainChar.getPosition();
            if (start != null && target != null && !start.equals(target)) {
                return findFirstStepOnShortestPath(start, target);
            } else {
                return null;
            }
        }   
    
    }

    /**
     * Finds the first step on the shortest path from start to target using breadth-first search.
     * @param start the starting position
     * @param target the target position
     * @return the first step on the shortest path from start to target, or null if no path exists
     */
    private Position findFirstStepOnShortestPath(Position start, Position target) {
        Map<Position, Position> parent = buildParentMap(start, target);
        if (!parent.containsKey(target)) {
            return null;
        }

        return reconstructFirstStep(start, target, parent);
    }

    /**
     * Runs breadth-first search from the start position until the target is found or no path exists.
     *
     * @param start the starting position
     * @param target the target position
     * @return a map storing each visited position and the position it came from
     */
    private Map<Position, Position> buildParentMap(Position start, Position target) {
        Deque<Position> queue = new ArrayDeque<>();
        Map<Position, Position> parent = new HashMap<>();

        queue.add(start);
        parent.put(start, null);

        while(!queue.isEmpty()) {
            Position current = queue.removeFirst();
            if (current.equals(target)) {
                break;
            }

            addUnvisitedNeighbors(current, queue, parent);
        }

        return parent;
    }

    /**
     * Adds all reachable, unvisited neighboring positions for the current search step.
     *
     * @param current the position currently being explored
     * @param queue queue of positions to visit next
     * @param parent map of visited positions to the position they were reached from
     */
    private void addUnvisitedNeighbors(Position current, Deque<Position> queue, Map<Position, Position> parent) {
        for (Position next : getValidNeighbors(current)) {
            if (!parent.containsKey(next)) {
                parent.put(next, current);
                queue.addLast(next);
            }
        }
    }

    /**
     * Collects neighboring positions that this enemy can move into.
     *
     * @param current the current position
     * @return list of valid neighboring positions
     */
    private List<Position> getValidNeighbors(Position current) {
        List<Position> moves = new ArrayList<>(4);
        addIfValid(moves, nextPosition(current, Direction.UP));
        addIfValid(moves, nextPosition(current, Direction.DOWN));
        addIfValid(moves, nextPosition(current, Direction.LEFT));
        addIfValid(moves, nextPosition(current, Direction.RIGHT));
        return moves;
    }

    /**
     * Backtracks from the target to identify the first move on the shortest path.
     *
     * @param start the starting position
     * @param target the target position
     * @param parent map of visited positions to the position they were reached from
     * @return the first step from start toward target
     */
    private Position reconstructFirstStep(Position start, Position target, Map<Position, Position> parent) {
        Position step = target;
        Position prev = parent.get(step);

        while (prev != null && !prev.equals(start)) {
            step = prev;
            prev = parent.get(step);
        }

        return step;

    }

    /**
     * Moves enemy to the given position if it's valid.
     * 
     * @param pos the position to move to
     */
    private void moveTo(Position pos) {
        if (pos == null) return;
        if (!canEnter(pos)) return;

        Cell from = getBoard().getCell(getPosition());
        Cell to = getBoard().getCell(pos);
        Actor occupant = to.getOccupant();

        if (from.getOccupant() == this) {
            from.clearOccupant();
        }

        if (occupant == null || occupant == this) {
            to.setOccupant(this);
        }

        setPositionDirect(pos);

    }

    /**
     * Helper method to check if a position is valid and add it to the list of possible moves.
     * @param moves the list of possible moves to add to
     * @param pos the position to check and add
     */
    private void addIfValid(List<Position> moves, Position pos) {
        if (canEnter(pos)) {
            moves.add(pos);
        }
    }

    /**
     * Overrides the canEnter method to allow moving onto the main character's position.
      * 
      * @param targetPos the position to check
      * @return true if the enemy can enter the cell, false otherwise
     */
    @Override
    protected boolean canEnter(Position targetPos) {
        if (targetPos == null) return false;
        if (!getBoard().isInside(targetPos)) return false;
        if (!getBoard().isWalkable(targetPos)) return false;

        Cell targetCell = getBoard().getCell(targetPos);
        Actor occupant = targetCell.getOccupant();
        return occupant == null || occupant == this || occupant instanceof MainCharacter;
    }

}

