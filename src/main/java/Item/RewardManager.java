package Item;

import java.util.List;
import java.util.Random;

import actor.MainCharacter;
import actor.MovingEnemy;
import engine.GameState;
import model.Board;
import model.Cell;
import model.Position;

/**
 * Manages the spawning, collection, and expiration of rewards within the game world.
 * Ensures rewards are placed in valid, walkable positions that do not overlap with 
 * players, enemies, or existing items.
 */

public class RewardManager {
    private final Random random;

    public RewardManager() {
        this.random = new Random();
    }

    /**
     * Checks if there is an enemy currently occupied at the specified position.
     *
     * @param pos     The position to check.
     * @param enemies The list of active moving enemies.
     * @return {@code true} if an enemy is found at the position; {@code false} otherwise.
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

    private Position findValidSpawnPosition(Board board, MainCharacter player, List<MovingEnemy> enemies) {
    int height = board.getHeight();
    int width = board.getWidth();

    for (int attempts = 0; attempts < 100; attempts++) {
        int r = random.nextInt(height);
        int c = random.nextInt(width);
        Position pos = new Position(r, c);

        if (!board.isWalkable(pos)) continue;
        if (board.isExit(pos) || pos.equals(board.getStartPos())) continue;
        if (player != null && pos.equals(player.getPosition())) continue;
        if (hasEnemyAt(pos, enemies)) continue;

        Cell cell = board.getCell(pos);
        if (cell == null || cell.hasItem()) continue;

        return pos; // ✅ valid position found
    }

    return null; // no valid position
}

    /**
     * Attempts to spawn a regular reward at a random valid location on the board.
     * The method tries up to 100 random positions before giving up.
     *
     * @param board   The game board where the reward will be placed.
     * @param player  The main character, used to avoid spawning on the player's head.
     * @param enemies The list of enemies, used to avoid spawning on enemies.
     * @param value   The score value of the regular reward.
     */

    public void spawnRegularReward(Board board, MainCharacter player, List<MovingEnemy> enemies,int value) {
        if (board == null) return;

        Position pos = findValidSpawnPosition(board, player, enemies);
        if (pos == null) return;

        board.getCell(pos).setItem(new RegularReward(value));
    }

    /**
     * Attempts to spawn a bonus reward with a specific expiration time.
     *
     * @param board     The game board.
     * @param player    The main character.
     * @param enemies   The list of active enemies.
     * @param value     The score value of the bonus reward.
     * @param spawnTick The game tick when this reward was created.
     * @param ttl       Time-to-live: how many ticks the reward remains active.
     */

    public void spawnBonusReward(Board board, MainCharacter player, List<MovingEnemy> enemies,int value,int spawnTick,
                                 int ttl) {
       if (board == null) return;

        Position pos = findValidSpawnPosition(board, player, enemies);
        if (pos == null) return;

        board.getCell(pos).setItem(new BonusReward(value, ttl, spawnTick));
    }

    /**
     * Iterates through the entire board and removes any {@link BonusReward} items 
     * that have exceeded their lifespan based on the current tick.
     *
     * @param board       The game board to clean.
     * @param currentTick The current time in the game engine.
     */

    public void removeExpiredBonusRewards(Board board, int currentTick) {
        if (board == null) {
            return;
        }

        for (int r = 0; r < board.getHeight(); r++) {
            for (int c = 0; c < board.getWidth(); c++) {
                Position pos = new Position(r, c);
                Cell cell = board.getCell(pos);

                if (cell == null || !cell.hasItem()) {
                    continue;
                }

                if (cell.getItem() instanceof BonusReward) {
                    BonusReward bonus = (BonusReward) cell.getItem();
                    if (bonus.isExpired(currentTick)) {
                        cell.clearItem();
                    }
                }
            }
        }
    }

    /**
     * Processes the collection of a reward when a player moves onto its cell.
     * Triggers the item's collection logic and removes it from the board.
     *
     * @param board     The game board.
     * @param pos       The position where the reward is being collected.
     * @param gameState The current game state to update (e.g., score).
     */

    public void collectRewardAtPosition(Board board, Position pos, GameState gameState) {
        if (board == null || pos == null || gameState == null) {
            return;
        }

        Cell cell = board.getCell(pos);
        if (cell == null || !cell.hasItem()) {
            return;
        }

        Item item = cell.getItem();
        item.onCollected(gameState);
        cell.clearItem();
    }
}