package engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class GameStateTest {
    
    /**
     * check that the constructor initializes the default values correctly.
     */
    @Test
    void constructor_shouldInitializeFieldsCorrectly() {
        GameState gameState = new GameState(3, 10);

        assertEquals(Status.PREVIEW, gameState.getStatus());
        assertEquals(10, gameState.getTime());
        assertEquals(0, gameState.getElapsedTicks());
        assertEquals(0, gameState.getStepUsed());
        assertEquals(3, gameState.getRegularRewardsRemaining());
        assertNotNull(gameState.getScoreCount());
    }

    /**
     * Verifies that the constructor throws an exception when the initial number
     * of regular rewards is negative.
     */
    @Test
    void constructor_shouldThrowException_whenInitialRegularRewardsRemainingIsNegative() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new GameState(-1, 10)
        );

        assertEquals("initialRegularRewardsRemaining cannot be negative", exception.getMessage());
    }

    /**
     * Verifies that the constructor throws an exception when the initial time is negative.
     */
    @Test
    void constructor_shouldThrowException_whenInitialTimeIsNegative() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new GameState(3, -1)
        );

        assertEquals("initialTime cannot be negative", exception.getMessage());
    }

    /**
     * Verifies that calling {@code setStatus} updates the game status correctly.
     */
    @Test
    void setStatus_shouldUpdateStatus() {
        GameState gameState = new GameState(3, 10);

        gameState.setStatus(Status.RUNNING);

        assertEquals(Status.RUNNING, gameState.getStatus());
    }

    /**
     * Verifies that calling {@code setStatus} with {@code null} throws an exception.
     */
    @Test
    void setStatus_shouldThrowException_whenStatusIsNull() {
        GameState gameState = new GameState(3, 10);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameState.setStatus(null)
        );

        assertEquals("status cannot be null", exception.getMessage());
    }

    /**
     * Verifies that {@code isLose} returns true when the status is {@code LOST}.
     */
    @Test
    void isLose_shouldReturnTrue_whenStatusIsLost() {
        GameState gameState = new GameState(3, 10);
        gameState.setStatus(Status.LOST);

        assertTrue(gameState.isLose());
    }

    /**
     * Verifies that {@code isLose} returns false when the status is not {@code LOST}.
     */
    @Test
    void isLose_shouldReturnFalse_whenStatusIsNotLost() {
        GameState gameState = new GameState(3, 10);

        assertFalse(gameState.isLose());
    }

    /**
     * Verifies that {@code updateTime} decreases the remaining time by one
     * and increments elapsed ticks by one.
     */
    @Test
    void updateTime_shouldDecreaseTimeAndIncreaseElapsedTicks() {
        GameState gameState = new GameState(3, 10);

        gameState.updateTime();

        assertEquals(9, gameState.getTime());
        assertEquals(1, gameState.getElapsedTicks());
    }

    /**
     * Verifies that {@code updateTime} does not reduce time below zero.
     */
    @Test
    void updateTime_shouldNotMakeTimeNegative() {
        GameState gameState = new GameState(3, 0);

        gameState.updateTime();

        assertEquals(0, gameState.getTime());
        assertEquals(1, gameState.getElapsedTicks());
    }

    /**
     * Verifies that repeated calls to {@code updateTime} clamp the time at zero
     * while elapsed ticks continue to increase.
     */
    @Test
    void updateTime_shouldClampTimeToZero_afterMultipleUpdates() {
        GameState gameState = new GameState(3, 2);

        gameState.updateTime();
        gameState.updateTime();
        gameState.updateTime();

        assertEquals(0, gameState.getTime());
        assertEquals(3, gameState.getElapsedTicks());
    }

    /**
     * Verifies that {@code incrementStepUsed} increases the step count by one.
     */
    @Test
    void incrementStepUsed_shouldIncreaseStepUsedByOne() {
        GameState gameState = new GameState(3, 10);

        gameState.incrementStepUsed();

        assertEquals(1, gameState.getStepUsed());
    }

    /**
     * Verifies that repeated calls to {@code incrementStepUsed}
     * the number of steps used.
     */
    @Test
    void incrementStepUsed_shouldAccumulateOverMultipleCalls() {
        GameState gameState = new GameState(3, 10);

        gameState.incrementStepUsed();
        gameState.incrementStepUsed();
        gameState.incrementStepUsed();

        assertEquals(3, gameState.getStepUsed());
    }

    /**
     * Verifies that {@code decreaseRegularRewards} decreases the remaining
     * reward count when the current value is greater than zero.
     */
    @Test
    void decreaseRegularRewards_shouldDecreaseWhenGreaterThanZero() {
        GameState gameState = new GameState(3, 10);

        gameState.decreaseRegularRewards();

        assertEquals(2, gameState.getRegularRewardsRemaining());
    }

    /**
     * Verifies that {@code decreaseRegularRewards} does not reduce
     * the remaining reward count below zero.
     */
    @Test
    void decreaseRegularRewards_shouldNotGoBelowZero() {
        GameState gameState = new GameState(0, 10);

        gameState.decreaseRegularRewards();

        assertEquals(0, gameState.getRegularRewardsRemaining());
    }

    /**
     * Verifies that repeated calls to {@code decreaseRegularRewards}
     * clamp the remaining reward count at zero.
     */
    @Test
    void decreaseRegularRewards_shouldClampAtZero_afterMultipleCalls() {
        GameState gameState = new GameState(1, 10);

        gameState.decreaseRegularRewards();
        gameState.decreaseRegularRewards();
        gameState.decreaseRegularRewards();

        assertEquals(0, gameState.getRegularRewardsRemaining());
    }

/**
     * Verifies that {@code addCollection} correctly delegates to {@link ScoreCountTest}
     * and increases the collection score.
     */
    @Test
    void addCollection_shouldIncreaseCollectionScore() {
        GameState gameState = new GameState(3, 10);

        gameState.addCollection(5);
        gameState.addCollection(3);

        assertEquals(8, gameState.getScoreCount().getCollectionScore());
    }

    /**
     * Verifies that {@code addBonus} correctly delegates to {@link ScoreCountTest}
     * and increases the bonus score.
     */
    @Test
    void addBonus_shouldIncreaseBonusScore() {
        GameState gameState = new GameState(3, 10);

        gameState.addBonus(10);

        assertEquals(10, gameState.getScoreCount().getBonusScore());
    }

    /**
     * Verifies that {@code addPenalty} correctly delegates to {@link ScoreCountTest}
     * and increases the penalty score.
     */
    @Test
    void addPenalty_shouldIncreasePenaltyScore() {
        GameState gameState = new GameState(3, 10);

        gameState.addPenalty(4);

        assertEquals(4, gameState.getScoreCount().getPenaltyScore());
    }

    /**
     * Verifies that {@code setTimeScore} correctly delegates to {@link ScoreCountTest}
     * and updates the time score.
     */
    @Test
    void setTimeScore_shouldSetTimeScoreCorrectly() {
        GameState gameState = new GameState(3, 10);

        gameState.setTimeScore(20);

        assertEquals(20, gameState.getScoreCount().getTimeScore());
    }

    /**
     * Verifies that the total score is computed correctly after multiple
     * score components have been modified.
     */
    @Test
    void totalScore_shouldBeComputedCorrectly() {
        GameState gameState = new GameState(3, 10);

        gameState.addCollection(10);
        gameState.addBonus(5);
        gameState.addPenalty(3);
        gameState.setTimeScore(7);

        int total = gameState.getScoreCount().computeTotal();

        assertEquals(10 + 5 + 7 - 3, total);
    }

}
