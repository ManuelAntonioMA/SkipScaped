package LevelSection;

/**
 * Factory class responsible for creating Level instances
 * based on difficulty.
 *
 * This centralizes all difficulty-related parameter decisions.
 */
public final class LevelFactory {

    // EASY parameters
    private static final int EASY_ROWS = 8;
    private static final int EASY_COLS = 8;
    private static final int EASY_REGULAR = 5;
    private static final int EASY_BONUS = 2;
    private static final int EASY_TIME = 300;
    private static final int EASY_ENEMY = 1;
    private static final int EASY_TRAPS = 1;

    // MEDIUM parameters
    private static final int MEDIUM_ROWS = 12;
    private static final int MEDIUM_COLS = 12;
    private static final int MEDIUM_REGULAR = 10;
    private static final int MEDIUM_BONUS = 3;
    private static final int MEDIUM_TIME = 240;
    private static final int MEDIUM_ENEMY = 2;
    private static final int MEDIUM_TRAPS = 2;

    // HARD parameters
    private static final int HARD_ROWS = 16;
    private static final int HARD_COLS = 16;
    private static final int HARD_REGULAR = 15;
    private static final int HARD_BONUS = 5;
    private static final int HARD_TIME = 180;
    private static final int HARD_ENEMY = 3;
    private static final int HARD_TRAPS = 2;


    private LevelFactory() {
    }

        /**
     * Creates a Level with predefined parameters based on difficulty.
     *
     * @param id level identifier
     * @param difficulty selected difficulty
     * @return configured Level instance
     */
    public static Level create(String id, Difficulty difficulty) {
        LevelConfig config;
        String name;

        switch (difficulty) {
            case EASY:
                name = "Easy";
                config = new LevelConfig(
                        EASY_ROWS,
                        EASY_COLS,
                        EASY_REGULAR,
                        EASY_BONUS,
                        EASY_TIME,
                        EASY_ENEMY,
                        EASY_TRAPS
                );
                break;

            case MEDIUM:
                name = "Medium";
                config = new LevelConfig(
                        MEDIUM_ROWS,
                        MEDIUM_COLS,
                        MEDIUM_REGULAR,
                        MEDIUM_BONUS,
                        MEDIUM_TIME,
                        MEDIUM_ENEMY,
                        MEDIUM_TRAPS
                );
                break;

            case HARD:
                name = "Hard";
                config = new LevelConfig(
                        HARD_ROWS,
                        HARD_COLS,
                        HARD_REGULAR,
                        HARD_BONUS,
                        HARD_TIME,
                        HARD_ENEMY,
                        HARD_TRAPS
                );
                break;

            default:
                throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
        }

        return new Level(id, name, difficulty, config);
    }
}
