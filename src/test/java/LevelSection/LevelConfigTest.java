package LevelSection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LevelConfig}.
 *
 * <p>This test class verifies that all constructor arguments are stored
 * correctly and can be retrieved through getter methods.
 */
class LevelConfigTest {

    /**
     * Verifies that the constructor stores all configuration values correctly.
     */
    @Test
    void constructor_shouldStoreAllValuesCorrectly() {
        LevelConfig config = new LevelConfig(10, 12, 5, 2, 60, 3, 3);

        assertEquals(10, config.getRows());
        assertEquals(12, config.getCols());
        assertEquals(5, config.getRegularRewards());
        assertEquals(2, config.getBonusRewards());
        assertEquals(60, config.getTimeLimit());
        assertEquals(3, config.getEnemyCount());
        assertEquals(3, config.getTrapCount());
    }
}