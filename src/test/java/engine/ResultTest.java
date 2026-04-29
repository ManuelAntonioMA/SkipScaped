package engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResultTest {

    /**
     * Verifies that the constructor correctly initializes all fields.
     */
    @Test
    void constructor_shouldInitializeFieldsCorrectly() {
        ScoreCount scoreCount = new ScoreCount();
        Result result = new Result(Status.WON, 15, scoreCount, null);

        assertEquals(Status.WON, result.getStatus());
        assertEquals(15, result.getTime());
        assertSame(scoreCount, result.getScoreCount());
        assertNull(result.getLoseReason());
    }

    /**
     * Verifies that {@code isWin} returns true when status is WON.
     */
    @Test
    void isWin_shouldReturnTrue_whenStatusIsWon() {
        Result result = new Result(Status.WON, 10, new ScoreCount(), null);

        assertTrue(result.isWin());
    }

    /**
     * Verifies that {@code isWin} returns false when status is not WON.
     */
    @Test
    void isWin_shouldReturnFalse_whenStatusIsNotWon() {
        Result result = new Result(Status.LOST, 10, new ScoreCount(), null);

        assertFalse(result.isWin());
    }

    /**
     * Verifies that {@code isLose} returns true when status is LOST.
     */
    @Test
    void isLose_shouldReturnTrue_whenStatusIsLost() {
        Result result = new Result(Status.LOST, 10, new ScoreCount(), null);

        assertTrue(result.isLose());
    }

    /**
     * Verifies that {@code isLose} returns false when status is not LOST.
     */
    @Test
    void isLose_shouldReturnFalse_whenStatusIsNotLost() {
        Result result = new Result(Status.WON, 10, new ScoreCount(), null);

        assertFalse(result.isLose());
    }

    /**
     * Verifies that getTime returns the correct value.
     */
    @Test
    void getTime_shouldReturnConstructorValue() {
        Result result = new Result(Status.RUNNING, 20, new ScoreCount(), null);

        assertEquals(20, result.getTime());
    }

    /**
     * Verifies that getScoreCount returns the same object passed in.
     */
    @Test
    void getScoreCount_shouldReturnSameObject() {
        ScoreCount sc = new ScoreCount();
        Result result = new Result(Status.RUNNING, 5, sc, null);

        assertSame(sc, result.getScoreCount());
    }

    /**
     * Verifies that getLoseReason returns null when no reason is provided.
     */
    @Test
    void getLoseReason_shouldReturnNull_whenNotSet() {
        Result result = new Result(Status.WON, 10, new ScoreCount(), null);

        assertNull(result.getLoseReason());
    }

    /**
     * Verifies that getLoseReason returns the correct enum value when provided.
     */
    @Test
    void getLoseReason_shouldReturnCorrectReason() {
        ScoreCount sc = new ScoreCount();

        Result result = new Result(
                Status.LOST,
                0,
                sc,
                LoseReason.TIME_OUT
        );

        assertEquals(LoseReason.TIME_OUT, result.getLoseReason());
    }

    /**
     * Verifies that different lose reasons are handled correctly.
     */
    @Test
    void getLoseReason_shouldHandleDifferentReasons() {
        ScoreCount sc = new ScoreCount();

        Result r1 = new Result(Status.LOST, 0, sc, LoseReason.ENEMY_COLLISION);
        Result r2 = new Result(Status.LOST, 0, sc, LoseReason.TRAP_COLLISION);
        Result r3 = new Result(Status.LOST, 0, sc, LoseReason.SCORE_NEGATIVE);

        assertEquals(LoseReason.ENEMY_COLLISION, r1.getLoseReason());
        assertEquals(LoseReason.TRAP_COLLISION, r2.getLoseReason());
        assertEquals(LoseReason.SCORE_NEGATIVE, r3.getLoseReason());
    }
}
