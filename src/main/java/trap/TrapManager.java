package trap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import javax.crypto.spec.HKDFParameterSpec.Extract;

import actor.MainCharacter;
import actor.MovingEnemy;
import engine.GameState;
import model.Board;
import model.Cell;
import model.Position;

/**
 * Manages all trap-related behavior in the game.
 * 
 * Responsibilities include:
 * - Spawning trap pickups on valid board positions
 * - Handling player interactions with traps (pickup and triggering)
 * - Managing placed traps and their lifecycle
 * - Detecting and resolving enemy-trap collisions
 */

public class TrapManager {
    private final List<Trap> traps;
    private final Random random;

    public TrapManager() {
        this.traps = new ArrayList<>();
        this.random = new Random();
    }

    /**
     * Removes all traps from the game.
     */

    public void clear() {
        traps.clear();
    }

    /**
     * Returns a copy of the current trap list to preserve encapsulation.
     */

    public List<Trap> getTraps() {
        return new ArrayList<>(traps);
    }

    /**
     * Checks if there is an active trap at a given position.
     */

    public boolean hasTrapAt(Position pos) {
        if (pos == null) {
            return false;
        }

        for (Trap trap : traps) {
            if (trap != null && trap.isActive() && pos.equals(trap.getPosition())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether any enemy occupies the given position.
     * Used to prevent spawning traps on enemies.
     */

    public boolean hasEnemyAt(Position pos, List<MovingEnemy> enemies) {
        if (pos == null || enemies == null) {
            return false;
        }

        for (MovingEnemy enemy : enemies) {
            if (enemy != null && pos.equals(enemy.getPosition())) {
                return true;
            }
        }
        return false;
    }

     /**
     * Attempts to spawn a trap pickup at a valid random position.
     * 
     * A valid position must:
     * - Be walkable
     * - Not be the start or exit
     * - Not be occupied by the player, enemies, or existing traps/items
     * 
     * The method tries up to 100 random positions before giving up.
     */

    public void spawnTrapPickup(Board board, MainCharacter player, List<MovingEnemy> enemies) {
        if (board == null) {
            return;
        }

        int height = board.getHeight();
        int width = board.getWidth();

        for (int attempts = 0; attempts < 100; attempts++) {
            int r = random.nextInt(height);
            int c = random.nextInt(width);
            Position pos = new Position(r, c);

            if (!board.isWalkable(pos)) {
                continue;
            }
            if (board.isExit(pos) || pos.equals(board.getStartPos())) {
                continue;
            }
            if (player != null && pos.equals(player.getPosition())) {
                continue;
            }

            Cell cell = board.getCell(pos);
            if (cell != null && cell.hasItem()) {
                continue;
            }
            if (hasEnemyAt(pos, enemies) || hasTrapAt(pos)) {
                continue;
            }

            traps.add(new Trap(pos, TrapState.PICKUP));
            return;
        }
    }

    /**
     * Checks if the player is standing on a trap pickup.
     * If so, grants the player a trap and deactivates the pickup.
     */

    public void checkTrapPickup(MainCharacter player) {
        if (player == null) {
            return;
        }

        Position playerPos = player.getPosition();
        if (playerPos == null) {
            return;
        }

        for (Trap trap : traps) {
            if (trap != null && trap.isPickup() && playerPos.equals(trap.getPosition())) {
                player.addTrap();
                trap.deactivate();
                return;
            }
        }
    }

    /**
     * Places an armed trap at the player's current position.
     * The trap initially ignores the player until they leave the tile.
     */

    public void placeTrapAtPlayer(MainCharacter player) {
        if (player == null) {
            return;
        }

        Position playerPos = player.getPosition();
        if (playerPos == null) {
            return;
        }

        if (hasTrapAt(playerPos)) {
            return;
        }

        if (!player.useTrap()) {
            return;
        }

        Trap trap = new Trap(playerPos, TrapState.ARMED);
        trap.setIgnorePlayerUntilLeave(true);
        traps.add(trap);
    }

    /**
     * Updates trap safety so that traps no longer ignore the player
     * once the player has moved away from their position.
     */

    public void updatePlacedTrapSafety(MainCharacter player) {
        if (player == null) {
            return;
        }

        Position playerPos = player.getPosition();
        if (playerPos == null) {
            return;
        }

        for (Trap trap : traps) {
            if (trap != null && trap.isArmed() && trap.shouldIgnorePlayerUntilLeave()) {
                if (!playerPos.equals(trap.getPosition())) {
                    trap.setIgnorePlayerUntilLeave(false);
                }
            }
        }
    }

    /**
     * Checks if the player has triggered an armed trap.
     * 
     * @return true if a trap was triggered, false otherwise
     */

    public boolean playerTriggeredTrap(MainCharacter player) {
        if (player == null) {
            return false;
        }

        Position playerPos = player.getPosition();
        if (playerPos == null) {
            return false;
        }

        for (Trap trap : traps) {
            if (trap != null && trap.isArmed() && playerPos.equals(trap.getPosition())) {
                if (trap.shouldIgnorePlayerUntilLeave()) {
                    continue;
                }

                trap.deactivate();
                return true;
            }
        }

        return false;
    }

     /**
     * Finds an armed trap triggered by the given enemy.
     */

    private Trap findTriggeredTrap(MovingEnemy enemy) {
    Position pos = enemy.getPosition();

    for (Trap trap : traps) {
        if (trap != null && trap.isArmed() && pos.equals(trap.getPosition())) {
                return trap;
            }
        }
        return null;
    }

    /**
     * Removes the enemy from its board cell if present.
     */

    private void clearEnemyFromBoard(MovingEnemy enemy) {
        if (enemy.getBoard() == null) return;

        Position pos = enemy.getPosition();
        Cell cell = enemy.getBoard().getCell(pos);

        if (cell != null && cell.getOccupant() == enemy) {
            cell.clearOccupant();
        }
    }

    /**
     * Applies the effects of a trap collision on an enemy.
     */

    private void handleTrapCollision(MovingEnemy enemy, Trap trap, List<MovingEnemy> deadEnemies) {
        clearEnemyFromBoard(enemy);
        trap.deactivate();
        deadEnemies.add(enemy);
    }

    /**
     * Checks whether any enemies have collided with armed traps.
     * Removes defeated enemies and deactivates triggered traps.
     * 
     * @return true if at least one enemy was killed
     */

    public boolean checkEnemyTrapCollisions(List<MovingEnemy> enemies) {
        return checkEnemyTrapCollisions(enemies, null);
    }

    public boolean checkEnemyTrapCollisions(List<MovingEnemy> enemies, GameState gameState) {
        if (enemies == null) return false;

        boolean anyEnemyKilled = false;
        List<MovingEnemy> deadEnemies = new ArrayList<>();

        for (MovingEnemy enemy : enemies) {
            if (enemy == null || enemy.getPosition() == null) continue;

            Trap trap = findTriggeredTrap(enemy);
            if (trap != null) {
                handleTrapCollision(enemy, trap, deadEnemies);
                anyEnemyKilled = true;
                if (gameState != null) {
                    gameState.addTrapKillBonus();
                }
            }
        }

        enemies.removeAll(deadEnemies);
        return anyEnemyKilled;
    }

    /**
     * Removes all inactive or null traps from the list.
     */

    public void removeInactiveTraps() {
        traps.removeIf(trap -> trap == null || !trap.isActive());
    }
}
