package integration;

import LevelSection.Difficulty;
import LevelSection.Level;
import LevelSection.LevelFactory;
import LevelSection.LevelManager;
import engine.GameState;
import engine.LoseReason;
import engine.Result;
import engine.Status;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for GameState between LevelSelection,
 * game state initialization, score updates, and final result creation.
 */
class LevelAndGameStateTest {

    /**
     * Verifies that a level created by {@link LevelFactory} can be stored in
     * {@link LevelManager} and later retrieved with its expected configuration.
     */
    @Test
    void levelFactoryAndLevelManager_shouldWorkTogether() {
        Level easy = LevelFactory.create("easy-1", Difficulty.EASY);
        Level hard = LevelFactory.create("hard-1", Difficulty.HARD);

        LevelManager manager = new LevelManager(List.of(easy, hard));

        Level selected = manager.getLevel("hard-1");

        assertNotNull(selected);
        assertEquals("hard-1", selected.getId());
        assertEquals(Difficulty.HARD, selected.getDifficulty());
        assertEquals(16, selected.getConfig().getRows());
        assertEquals(16, selected.getConfig().getCols());
        assertEquals(15, selected.getConfig().getRegularRewards());
        assertEquals(180, selected.getConfig().getTimeLimit());
    }

    /**
     * Verifies that configuration produced by {@link LevelFactory} can be used
     * to initialize a {@link GameState} with the correct starting values.
     */
    @Test
    void levelConfig_shouldInitializeGameStateCorrectly() {
        Level medium = LevelFactory.create("medium-1", Difficulty.MEDIUM);

        GameState gameState = new GameState(
                medium.getConfig().getRegularRewards(),
                medium.getConfig().getTimeLimit()
        );

        assertEquals(Status.PREVIEW, gameState.getStatus());
        assertEquals(10, gameState.getRegularRewardsRemaining());
        assertEquals(240, gameState.getTime());
        assertEquals(0, gameState.getElapsedTicks());
        assertEquals(0, gameState.getStepUsed());
        assertEquals(0, gameState.getScoreCount().getTotalScore());
    }

    /**
     * Verifies that score updates made through {@link GameState} are reflected
     * in the embedded score object and preserved when creating a {@link Result}.
     */
    @Test
    void gameStateAndResult_shouldPreserveFinalScoringInformation() {
        GameState gameState = new GameState(5, 60);

        gameState.addCollection(10);
        gameState.addBonus(5);
        gameState.addPenalty(3);
        gameState.setTimeScore(8);
        gameState.setStatus(Status.WON);

        Result result = new Result(
                gameState.getStatus(),
                gameState.getTime(),
                gameState.getScoreCount(),
                null
        );

        assertTrue(result.isWin());
        assertFalse(result.isLose());
        assertEquals(Status.WON, result.getStatus());
        assertEquals(60, result.getTime());
        assertEquals(10, result.getScoreCount().getCollectionScore());
        assertEquals(5, result.getScoreCount().getBonusScore());
        assertEquals(3, result.getScoreCount().getPenaltyScore());
        assertEquals(8, result.getScoreCount().getTimeScore());
        assertEquals(20, result.getScoreCount().getTotalScore());
    }

    /**
     * Verifies that runtime progress updates in {@link GameState} can be turned
     * into a losing {@link Result} with the correct loss reason.
     */
    @Test
    void gameStateAndResult_shouldRepresentLoseOutcomeCorrectly() {
        GameState gameState = new GameState(3, 1);

        gameState.updateTime();
        gameState.updateTime();
        gameState.setStatus(Status.LOST);

        Result result = new Result(
                gameState.getStatus(),
                gameState.getTime(),
                gameState.getScoreCount(),
                LoseReason.TIME_OUT
        );

        assertTrue(gameState.isLose());
        assertTrue(result.isLose());
        assertEquals(0, result.getTime());
        assertEquals(LoseReason.TIME_OUT, result.getLoseReason());
    }

    /**
     * Verifies a simple end-to-end flow from level selection to state updates:
     * a selected level initializes the game, the player collects rewards,
     * and the remaining required rewards decrease accordingly.
     */
    @Test
    void selectedLevel_shouldDriveBasicGameplayProgress() {
        Level easy = LevelFactory.create("easy-1", Difficulty.EASY);
        LevelManager manager = new LevelManager(List.of(easy));

        Level selected = manager.getLevel("easy-1");
        GameState gameState = new GameState(
                selected.getConfig().getRegularRewards(),
                selected.getConfig().getTimeLimit()
        );

        gameState.addCollection(4);
        gameState.decreaseRegularRewards();
        gameState.incrementStepUsed();
        gameState.updateTime();

        assertEquals(4, gameState.getScoreCount().getCollectionScore());
        assertEquals(4, gameState.getRegularRewardsRemaining());
        assertEquals(1, gameState.getStepUsed());
        assertEquals(299, gameState.getTime());
        assertEquals(1, gameState.getElapsedTicks());
    }
}
