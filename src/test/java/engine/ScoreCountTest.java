package engine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScoreCountTest {

    /**
     * Verifies that all score fields are initialized to zero.
     */
    @Test
    void initialValues_shouldBeZero() {
        ScoreCount scoreCount = new ScoreCount();

        assertEquals(0, scoreCount.getCollectionScore());
        assertEquals(0, scoreCount.getBonusScore());
        assertEquals(0, scoreCount.getPenaltyScore());
        assertEquals(0, scoreCount.getTimeScore());
        assertEquals(0, scoreCount.getTotalScore());
    }

    /**
     * Verifies that {@code addCollection} correctly accumulates collection score.
     */
    @Test
    void addCollection_shouldAccumulate() {
        ScoreCount scoreCount = new ScoreCount();

        scoreCount.addCollection(5);
        scoreCount.addCollection(3);

        assertEquals(8, scoreCount.getCollectionScore());
    }

    /**
     * Verifies that {@code addBonus} correctly accumulates bonus score.
     */
    @Test
    void addBonus_shouldAccumulate() {
        ScoreCount scoreCount = new ScoreCount();

        scoreCount.addBonus(10);
        scoreCount.addBonus(2);

        assertEquals(12, scoreCount.getBonusScore());
    }

    /**
     * Verifies that {@code addPenalty} accumulates penalty score correctly.
     */
    @Test
    void addPenalty_shouldAccumulate() {
        ScoreCount scoreCount = new ScoreCount();

        scoreCount.addPenalty(4);
        scoreCount.addPenalty(1);

        assertEquals(5, scoreCount.getPenaltyScore());
    }

    /**
     * Verifies that {@code setTimeScore} replaces the previous time score value.
     */
    @Test
    void setTimeScore_shouldOverrideValue() {
        ScoreCount scoreCount = new ScoreCount();

        scoreCount.setTimeScore(20);
        scoreCount.setTimeScore(5);

        assertEquals(5, scoreCount.getTimeScore());
    }

    /**
     * Verifies that {@code computeTotal} returns and stores the correct total score
     * when all score components are present.
     */
    @Test
    void computeTotal_shouldCalculateCorrectly() {
        ScoreCount scoreCount = new ScoreCount();

        scoreCount.addCollection(10);
        scoreCount.addBonus(5);
        scoreCount.setTimeScore(7);
        scoreCount.addPenalty(3);

        int total = scoreCount.computeTotal();

        assertEquals(19, total);
        assertEquals(19, scoreCount.getTotalScore());
    }

    /**
     * Verifies that {@code computeTotal} returns zero when no score values
     * have been added or set.
     */
    @Test
    void computeTotal_shouldHandleZeroValues() {
        ScoreCount scoreCount = new ScoreCount();

        int total = scoreCount.computeTotal();

        assertEquals(0, total);
        assertEquals(0, scoreCount.getTotalScore());
    }

    /**
     * Verifies that {@code computeTotal} can produce a negative result
     * when penalty score is greater than all positive scores combined.
     */
    @Test
    void computeTotal_shouldHandleOnlyPenalty() {
        ScoreCount scoreCount = new ScoreCount();

        scoreCount.addPenalty(5);

        int total = scoreCount.computeTotal();

        assertEquals(-5, total);
        assertEquals(-5, scoreCount.getTotalScore());
    }
}