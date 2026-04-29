package LevelSection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LevelSection}.
 *
 * <p>This test class verifies:
 * <ul>
 *     <li>constructor initialization</li>
 *     <li>null argument validation</li>
 *     <li>getter correctness</li>
 * </ul>
 */
class LevelTest {

    /**
     * Verifies that the constructor correctly initializes all fields.
     */
    @Test
    void constructor_shouldInitializeFieldsCorrectly() {
        LevelConfig config = new LevelConfig(8, 8, 5, 2, 60, 1, 1);
        Level level = new Level("1", "Test", Difficulty.EASY, config);

        assertEquals("1", level.getId());
        assertEquals("Test", level.getName());
        assertEquals(Difficulty.EASY, level.getDifficulty());
        assertSame(config, level.getConfig());
    }

    /**
     * Verifies that the constructor throws a {@link NullPointerException}
     * when the level id is null.
     */
    @Test
    void constructor_shouldThrowException_whenIdIsNull() {
        LevelConfig config = new LevelConfig(8, 8, 5, 2, 60, 1, 1);

        assertThrows(NullPointerException.class, () ->
                new Level(null, "Test", Difficulty.EASY, config));
    }

    /**
     * Verifies that the constructor throws a {@link NullPointerException}
     * when the level name is null.
     */
    @Test
    void constructor_shouldThrowException_whenNameIsNull() {
        LevelConfig config = new LevelConfig(8, 8, 5, 2, 60, 1, 1);

        assertThrows(NullPointerException.class, () ->
                new Level("1", null, Difficulty.EASY, config));
    }

    /**
     * Verifies that the constructor throws a {@link NullPointerException}
     * when the difficulty is null.
     */
    @Test
    void constructor_shouldThrowException_whenDifficultyIsNull() {
        LevelConfig config = new LevelConfig(8, 8, 5, 2, 60, 1, 1);

        assertThrows(NullPointerException.class, () ->
                new Level("1", "Test", null, config));
    }

    /**
     * Verifies that the constructor throws a {@link NullPointerException}
     * when the configuration object is null.
     */
    @Test
    void constructor_shouldThrowException_whenConfigIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Level("1", "Test", Difficulty.EASY, null));
    }
}