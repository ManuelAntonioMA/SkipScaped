package LevelSection;

import java.util.Objects;

/**
 * a single  level:
 *
 *  an identifier
 *  a display name
 *  a difficulty
 *  a configuration object
 *
 * It does NOT contain runtime state or game logic.
 */
public class Level {

    private final String id;
    private final String name;
    private final Difficulty difficulty;
    private final LevelConfig config;

    /**
     * Creates a new Level.
     *
     * @param id unique identifier
     * @param name display name
     * @param difficulty difficulty level
     * @param config configuration parameters
     */
    public Level(String id, String name,
                 Difficulty difficulty, LevelConfig config) {

        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.difficulty = Objects.requireNonNull(difficulty);
        this.config = Objects.requireNonNull(config);
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Difficulty getDifficulty() {
        return difficulty;
    }
    public LevelConfig getConfig() {
        return config;
    }
}