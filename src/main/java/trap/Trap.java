package trap;

import model.Position;

/**
 * Represents a trap in the game world.
 * 
 * A trap can exist in two main states:
 * - PICKUP: can be collected by the player
 * - ARMED: can be triggered by enemies or the player
 * 
 * Traps can also temporarily ignore the player after being placed
 * to prevent immediate self-triggering.
 */

public class Trap {
    private Position position;
    private TrapState state;
    private boolean active;
    private boolean ignorePlayerUntilLeave;

    /**
     * Creates a new trap at a given position with a specified state.
     * 
     * @param position the position of the trap on the board
     * @param state the initial state of the trap (PICKUP or ARMED)
     * @throws IllegalArgumentException if state is null
     */

    public Trap(Position position, TrapState state) {
        if (state == null) {
            throw new IllegalArgumentException("state cannot be null");
        }

        this.position = position;
        this.state = state;
        this.active = true;
        this.ignorePlayerUntilLeave = false;
    }

    public Position getPosition() {
        return position;
    }

    public TrapState getState() {
        return state;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isPickup() {
        return active && state == TrapState.PICKUP;
    }

    public boolean isArmed() {
        return active && state == TrapState.ARMED;
    }

    public boolean shouldIgnorePlayerUntilLeave() {
        return ignorePlayerUntilLeave;
    }

    /**
     * Sets whether the trap should temporarily ignore the player.
     */

    public void setIgnorePlayerUntilLeave(boolean ignorePlayerUntilLeave) {
        this.ignorePlayerUntilLeave = ignorePlayerUntilLeave;
    }

    /**
     * Arms the trap at a new position.
     * 
     * This transitions the trap into an active armed state.
     * 
     * @param newPosition the new position for the trap
     * @throws IllegalArgumentException if newPosition is null
     */

    public void armAt(Position newPosition) {
        if (newPosition == null) {
            throw new IllegalArgumentException("newPosition cannot be null");
        }

        this.position = newPosition;
        this.state = TrapState.ARMED;
        this.active = true;
    }

    /**
     * Deactivates the trap and clears its state.
     * 
     * Once deactivated:
     * - The trap is no longer active
     * - Its position is cleared
     * - It no longer ignores the player
     */

    public void deactivate() {
        this.active = false;
        this.position = null;
        this.ignorePlayerUntilLeave = false;
    }
}