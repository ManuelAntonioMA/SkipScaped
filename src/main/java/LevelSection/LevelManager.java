package LevelSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Manages a collection of Level objects.
 *
 * Provides lookup and read-only access to available levels.
 */
public class LevelManager {

    private final List<Level> levels;

    /**
     * Constructs a LevelManager with predefined levels.
     *
     * @param levels list of levels to manage
     */
    public LevelManager(List<Level> levels) {
        this.levels = new ArrayList<>(levels);
    }

    /**
     * Returns an unmodifiable list of all levels.
     *
     * @return list of levels
     */
    public List<Level> getLevels() {
        return Collections.unmodifiableList(levels);
    }

    /**
     * Finds a Level by its identifier.
     *
     * @param id level identifier
     * @return matching Level or null if not found
     */
    public Level getLevel(String id) {
        for (Level level : levels) {
            if (level.getId().equals(id)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown level id: " + id);
    }
}