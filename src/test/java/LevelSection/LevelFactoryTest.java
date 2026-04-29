package LevelSection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LevelFactory}.
 *
 * <p>This test class verifies that the factory creates correctly configured
 * {@link Level} objects for each supported {@link Difficulty}.
 */
class LevelFactoryTest {

    /**
     * Verifies that creating an EASY level returns a Level object
     * with the expected metadata and configuration values.
     */
    @Test
    void create_shouldReturnEasyLevel() {
        Level level = LevelFactory.create("1", Difficulty.EASY);

        assertEquals("1", level.getId());
        assertEquals("Easy", level.getName());
        assertEquals(Difficulty.EASY, level.getDifficulty());

        LevelConfig config = level.getConfig();
        assertEquals(8, config.getRows());
        assertEquals(8, config.getCols());
        assertEquals(5, config.getRegularRewards());
        assertEquals(2, config.getBonusRewards());
        assertEquals(300, config.getTimeLimit());
        assertEquals(1, config.getEnemyCount());
    }

    /**
     * Verifies that creating a MEDIUM level returns a Level object
     * with the expected metadata and configuration values.
     */
    @Test
    void create_shouldReturnMediumLevel() {
        Level level = LevelFactory.create("2", Difficulty.MEDIUM);

        assertEquals("2", level.getId());
        assertEquals("Medium", level.getName());
        assertEquals(Difficulty.MEDIUM, level.getDifficulty());

        LevelConfig config = level.getConfig();
        assertEquals(12, config.getRows());
        assertEquals(12, config.getCols());
        assertEquals(10, config.getRegularRewards());
        assertEquals(3, config.getBonusRewards());
        assertEquals(240, config.getTimeLimit());
        assertEquals(2, config.getEnemyCount());
    }

    /**
     * Verifies that creating a HARD level returns a Level object
     * with the expected metadata and configuration values.
     */
    @Test
    void create_shouldReturnHardLevel() {
        Level level = LevelFactory.create("3", Difficulty.HARD);

        assertEquals("3", level.getId());
        assertEquals("Hard", level.getName());
        assertEquals(Difficulty.HARD, level.getDifficulty());

        LevelConfig config = level.getConfig();
        assertEquals(16, config.getRows());
        assertEquals(16, config.getCols());
        assertEquals(15, config.getRegularRewards());
        assertEquals(5, config.getBonusRewards());
        assertEquals(180, config.getTimeLimit());
        assertEquals(3, config.getEnemyCount());
    }
}
