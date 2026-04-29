package spawn;

import java.util.List;
import java.util.Random;

import LevelSection.Difficulty;
import LevelSection.Level;
import actor.MainCharacter;
import actor.MovingEnemy;
import engine.GameState;
import model.Board;
import model.Position;
import trap.TrapManager;

public class DifficultySpawnManager {
    private static final int HARD_SPAWN_STEP_INTERVAL = 15;
    private static final int ENEMY_SPEED = 2;

    private final Random random;
    private int lastHardSpawnStep;

    public DifficultySpawnManager() {
        this.random = new Random();
        this.lastHardSpawnStep = 0;
    }

    public void reset() {
        lastHardSpawnStep = 0;
    }

    public void update(Level level, Board board, MainCharacter player,
                       List<MovingEnemy> enemies, GameState gameState,
                       TrapManager trapManager) {
        if (!shouldTriggerHardSpawn(level, gameState)) {
            return;
        }

        spawnOneEnemy(board, player, enemies, trapManager);
        trapManager.spawnTrapPickup(board, player, enemies);

        lastHardSpawnStep = gameState.getStepUsed();
    }

    private boolean shouldTriggerHardSpawn(Level level, GameState gameState) {
        if (level == null || gameState == null) {
            return false;
        }

        if (level.getDifficulty() != Difficulty.HARD) {
            return false;
        }

        int steps = gameState.getStepUsed();

        if (steps < HARD_SPAWN_STEP_INTERVAL) {
            return false;
        }

        if (steps % HARD_SPAWN_STEP_INTERVAL != 0) {
            return false;
        }

        return steps != lastHardSpawnStep;
    }

    private void spawnOneEnemy(Board board, MainCharacter player,
                               List<MovingEnemy> enemies, TrapManager trapManager) {
        Position pos = findValidEnemySpawnPosition(board, player, enemies, trapManager);
        if (pos == null) {
            return;
        }

        enemies.add(new MovingEnemy(pos, board, ENEMY_SPEED));
    }

    private Position findValidEnemySpawnPosition(Board board, MainCharacter player,
                                                 List<MovingEnemy> enemies,
                                                 TrapManager trapManager) {
        if (board == null || player == null || enemies == null || trapManager == null) {
            return null;
        }

        int height = board.getHeight();
        int width = board.getWidth();

        for (int attempts = 0; attempts < 100; attempts++) {
            int r = random.nextInt(height);
            int c = random.nextInt(width);
            Position pos = new Position(r, c);

            if (!board.isWalkable(pos)) continue;
            if (board.isExit(pos) || pos.equals(board.getStartPos())) continue;
            if (pos.equals(player.getPosition())) continue;
            if (trapManager.hasEnemyAt(pos, enemies)) continue;

            return pos;
        }

        return null;
    }
}