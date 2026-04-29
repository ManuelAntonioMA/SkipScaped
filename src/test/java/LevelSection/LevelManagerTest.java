package LevelSection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LevelManager}.
 *
 * <p>This test class verifies:
 * <ul>
 *     <li>read-only access to the managed level list</li>
 *     <li>successful level lookup by id</li>
 *     <li>lookup behavior when no matching id exists</li>
 * </ul>
 */
class LevelManagerTest {

    /**
     * Verifies that {@code getLevels()} returns an unmodifiable list.
     */
    @Test
    void getLevels_shouldReturnUnmodifiableList() {
        Level level = LevelFactory.create("1", Difficulty.EASY);
        LevelManager manager = new LevelManager(List.of(level));

        List<Level> levels = manager.getLevels();

        assertThrows(UnsupportedOperationException.class, () -> levels.add(level));
    }

    /**
     * Verifies that {@code getLevel()} returns the correct level
     * when the requested id exists.
     */
    @Test
    void getLevel_shouldReturnCorrectLevel_whenIdExists() {
        Level level1 = LevelFactory.create("1", Difficulty.EASY);
        Level level2 = LevelFactory.create("2", Difficulty.HARD);

        LevelManager manager = new LevelManager(List.of(level1, level2));

        Level result = manager.getLevel("2");

        assertEquals(level2, result);
    }

    /**
     * Verifies that {@code getLevel()} returns null
     * when the requested id does not exist.
     */
//    @Test
//    void getLevel_shouldReturnNull_whenIdNotFound() {
//        Level level = LevelFactory.create("1", Difficulty.EASY);
//        LevelManager manager = new LevelManager(List.of(level));
//
//        Level result = manager.getLevel("999");
//
//        assertNull(result);
//    }
    @Test
    void getLevel_shouldThrowIllegalArgumentException_whenIdNotFound() {
        Level level = LevelFactory.create("1", Difficulty.EASY);
        LevelManager manager = new LevelManager(List.of(level));

        assertThrows(IllegalArgumentException.class, () -> manager.getLevel("999"));
    }

    /**
     * Verifies that {@code getLevel()} can correctly find
     * the first level in the managed list.
     */
    @Test
    void getLevel_shouldFindFirstLevelInList() {
        Level level1 = LevelFactory.create("1", Difficulty.EASY);
        Level level2 = LevelFactory.create("2", Difficulty.MEDIUM);

        LevelManager manager = new LevelManager(List.of(level1, level2));

        Level result = manager.getLevel("1");

        assertEquals(level1, result);
    }
}