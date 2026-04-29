package engine;

import actor.Direction;

/**
 * Represents the direction movement and the action (with the trap) that the
 * player can take in one turn
 */
public class PlayerTurn {
    private final Direction direction;
    private final Action action;

    /**
     * Represents the direction movement and the action (with the trap) that the
     * player can take in one turn
     * @param direction {@link Direction} movement
     * @param action {@link Action} with trap
     */
    public PlayerTurn(Direction direction, Action action) {
        this.direction = direction;
        this.action = action;
    }

    /**
     * Getter that returns the {@link Direction} of movement
     * @return {@link Direction}
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Getter that returns the {@link Action} of player
     * @return {@link Action}
     */
    public Action getAction() {
        return action;
    }
}
