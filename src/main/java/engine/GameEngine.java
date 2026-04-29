package engine;

import java.util.ArrayList;
import java.util.List;

import Item.RewardManager;
import LevelSection.Level;
import actor.Direction;
import actor.MainCharacter;
import actor.MovingEnemy;
import model.Board;
import model.Position;
import spawn.DifficultySpawnManager;
import trap.TrapManager;
import LevelSection.LevelConfig;

public class GameEngine {
    private static final int BONUS_SPAWN_INTERVAL = 6;
    private static final int BONUS_SCORE = 25;
    private static final int BONUS_TTL = 20;

    private Board board;
    private MainCharacter player;
    private List<MovingEnemy> enemies;
    private GameState gameState;
    private int tickCount;
    private Level currentLevel;
    private LoseReason loseReason;
    private final TrapManager trapManager;
    private final RewardManager rewardManager;
    private final DifficultySpawnManager difficultySpawnManager;

    /**
     * Represents the engine of the game. Runs the game and ensures its turn-based nature.
     */
    public GameEngine() {
        this.enemies = new ArrayList<>();
        this.tickCount = 0;
        this.loseReason = null;
        this.trapManager = new TrapManager();
        this.rewardManager = new RewardManager();
        this.difficultySpawnManager = new DifficultySpawnManager();
    }

    public void start(Board board, Level level, MainCharacter player,
                      List<MovingEnemy> enemies, GameState gameState) {
        if (board == null) {
            throw new IllegalArgumentException("board cannot be null");
        }
        if (player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }
        if (gameState == null) {
            throw new IllegalArgumentException("gameState cannot be null");
        }

        this.board = board;
        this.player = player;
        this.enemies = (enemies == null) ? new ArrayList<>() : new ArrayList<>(enemies);
        this.gameState = gameState;
        this.currentLevel = level;
        this.tickCount = 0;
        this.loseReason = null;
        difficultySpawnManager.reset();
        trapManager.clear();

        this.gameState.setStatus(Status.RUNNING);
    }

    /**
     * Represents the turn-based mechanism of the game: one turn (tick) after a
     * single player AND all enemies move. Trap activation does not take a turn.
     * @param playerTurn {@link PlayerTurn} input of the player: direction and trap action, if has trap
     */
    public void turnFlow(PlayerTurn playerTurn) {
        if (!isTurnRunnable()) {
            return;
        }

        boolean turnConsumed = processPlayerPhase(playerTurn);
        if (!turnConsumed) {
            return;
        }

        if (evaluateGameEndAfterPlayerPhase()) {
            return;
        }

        processEnemyPhase();
        if (evaluateLoseAfterEnemyPhase()) {
            gameState.setStatus(Status.LOST);
            return;
        }

        tickCount++;
    }

    private boolean isTurnRunnable() {
        return gameState != null && gameState.getStatus() == Status.RUNNING;
    }

    private boolean processPlayerPhase(PlayerTurn playerTurn) {
        boolean turnConsumed = handlePlayerTurn(playerTurn);
        if (!turnConsumed) {
            return false;
        }

        trapManager.updatePlacedTrapSafety(player);
        return true;
    }

    private boolean evaluateGameEndAfterPlayerPhase() {
        if (trapManager.playerTriggeredTrap(player)) {
            loseReason = LoseReason.TRAP_COLLISION;
            gameState.setStatus(Status.LOST);
            return true;
        }

        if (checkWin()) {
            gameState.setStatus(Status.WON);
            return true;
        }

        if (checkLose()) {
            gameState.setStatus(Status.LOST);
            return true;
        }

        return false;
    }

    private void processEnemyPhase() {
        handleEnemyTurn();
        trapManager.checkEnemyTrapCollisions(enemies, gameState);
        trapManager.removeInactiveTraps();
        updateBonusRewards();
        difficultySpawnManager.update(currentLevel, board, player, enemies, gameState, trapManager);
    }

    private boolean evaluateLoseAfterEnemyPhase() {
        return checkLose();
    }

    private boolean handlePlayerTurn(PlayerTurn playerTurn) {
        if (playerTurn == null) {
            return false;
        }

        boolean moved = handlePlayerMovement(playerTurn.getDirection());
        boolean acted = handlePlayerAction(playerTurn.getAction());

        if (!moved && !acted) {
            return false;
        }

        if (moved && gameState != null) {
            gameState.incrementStepUsed();
        }

        collectItemAtPlayerPosition(); // Check if collected item
        trapManager.checkTrapPickup(player); // Check if collected trap

        return true;
    }

    private boolean handlePlayerMovement(Direction direction) {
        if (player == null) {
            return false;
        }

        if (direction == null || direction == Direction.NONE) {
            return false;
        }

        Position before = player.getPosition();
        player.handleInput(direction);
        Position after = player.getPosition();

        return before != null && !before.equals(after);
    }

    private boolean handlePlayerAction(Action action) {
        if (action == null || action == Action.NONE) {
            return false;
        }

        if (action == Action.PLACE_TRAP) {
            int before = player.getTrapInventory();
            trapManager.placeTrapAtPlayer(player);
            return player != null && player.getTrapInventory() < before;
        }

        return false;
    }

    private void handleEnemyTurn() {
        if (player == null || enemies == null) {
            return;
        }

        for (MovingEnemy enemy : enemies) {
            if (enemy != null) {
                enemy.update(player);
            }
        }
    }

    private boolean checkWin() {
        if (board == null || player == null || gameState == null) {
            return false;
        }

        boolean noRewardsLeft = gameState.getRegularRewardsRemaining() == 0;
        boolean atExit = board.isExit(player.getPosition());

        return noRewardsLeft && atExit;
    }

    private boolean checkLose() {
        if (gameState == null) {
            return false;
        }

        if (player != null && enemies != null) {
            Position playerPos = player.getPosition();
            if (playerPos != null) {
                for (MovingEnemy enemy : enemies) {
                    if (enemy != null && playerPos.equals(enemy.getPosition())) {
                        loseReason = LoseReason.ENEMY_COLLISION;
                        return true;
                    }
                }
            }
        }

        if (gameState.getTime() <= 0) {
            loseReason = LoseReason.TIME_OUT;
            return true;
        }
        if (gameState.getScoreCount().getTotalScore() < 0) {
            loseReason = LoseReason.SCORE_NEGATIVE;
            return true;
        }

        return false;
    }

    private void updateBonusRewards() {
        rewardManager.removeExpiredBonusRewards(board, tickCount);

        if (tickCount % BONUS_SPAWN_INTERVAL == 0) {
            rewardManager.spawnBonusReward(
                    board,
                    player,
                    enemies,
                    BONUS_SCORE,
                    tickCount,
                    BONUS_TTL
            );
        }
    }

    private void collectItemAtPlayerPosition() {
        if (player == null) {
            return;
        }

        rewardManager.collectRewardAtPosition(board, player.getPosition(), gameState);
    }

    /**
     * Getter method for status
     * @return {@link Status}
     */
    public Status getStatus() {
        return (gameState == null) ? Status.PREVIEW : gameState.getStatus();
    }

    /**
     * Getter method for tickCount
     * @return tickCount
     */
    public int getTickCount() {
        return tickCount;
    }

    /**
     * Getter method for Board
     * @return {@link Board}
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Getter method for player; link {@link PlayerTurn}
     * @return player; link {@link PlayerTurn}
     */
    public MainCharacter getPlayer() {
        return player;
    }

    /**
     * Getter method for enemies
     * @return list of enemies
     */
    public List<MovingEnemy> getEnemies() {
        return (enemies == null) ? new ArrayList<>() : new ArrayList<>(enemies);
    }

    /**
     * Getter method for TrapManager
     * @return {@link TrapManager}
     */

    public void updateRealTime() {
        if (gameState == null || gameState.getStatus() != Status.RUNNING) {
            return;
        }
        gameState.updateTime();
        if (gameState.getTime() <= 0) {
            loseReason = LoseReason.TIME_OUT;
            gameState.setStatus(Status.LOST);
        }
    }

    public TrapManager getTrapManager() {
        return trapManager;
    }

    /**
     * Getter method for GameState
     * @return {@link GameState}
     */
    public GameState getGameState() {
        return gameState;
    }

    public int getRemainingRewards() {
        return (gameState == null) ? 0 : gameState.getRegularRewardsRemaining();
    }

    public int getCurrentScore() {
        return (gameState == null) ? 0 : gameState.getScoreCount().getTotalScore();
    }

    public int getTimeLeft() {
        return (gameState == null) ? 0 : gameState.getTime();
    }

    public int getStepsUsed() {
        return (gameState == null) ? 0 : gameState.getStepUsed();
    }
    /**
     * Builds the results after GameOver
     */
    public Result buildResult() {
        if (gameState == null) {
            throw new IllegalStateException("engine.GameState is not initialized.");
        }

        if (gameState.getStatus() == Status.WON) {
            gameState.setTimeScore(computeTimeBonus());
            return new Result(Status.WON, gameState.getTime(), gameState.getScoreCount(), null);
        }

        if (gameState.getStatus() == Status.LOST) {
            return new Result(Status.LOST, gameState.getTime(), gameState.getScoreCount(), loseReason);
        }

        throw new IllegalStateException("Game not finished yet.");
    }

    private int computeTimeBonus() {
        if (gameState == null || currentLevel == null) {
            return 0;
        }

        LevelConfig config = currentLevel.getConfig();
        int timeLimit = config.getTimeLimit();
        int timeLeft = gameState.getTime();
        return Math.min(timeLimit, Math.max(0, timeLeft));
    }
}
