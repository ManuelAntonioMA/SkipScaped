package LevelSection;

/**
 * Immutable configuration object that stores all numeric
 * parameters required to initialize a level.
 *
 * This class does NOT contain game logic.
 * It only holds configuration data.
 */
public class LevelConfig {

    private final int rows;
    private final int cols;
    private final int regularRewards;
    private final int bonusRewards;
    private final int timeLimit;
    private final int enemyCount;
    private final int trapCount;

    /**
     * Constructs a LevelConfig with fixed parameters.
     *
     * @param rows number of board rows
     * @param cols number of board columns
     * @param regularRewards number of required collectible rewards
     * @param bonusRewards number of optional bonus rewards
     * @param timeLimit countdown time limit
     * @param enemyCount number of enemies
     */
    public LevelConfig(int rows, int cols,
                       int regularRewards, int bonusRewards,
                       int timeLimit, int enemyCount,
                       int trapCount) {

        if (rows <= 0) {
            throw new IllegalArgumentException("rows must be greater than 0");
        }
        if (cols <= 0) {
            throw new IllegalArgumentException("cols must be greater than 0");
        }
        if (regularRewards < 0) {
            throw new IllegalArgumentException("regularRewards cannot be negative");
        }
        if (bonusRewards < 0) {
            throw new IllegalArgumentException("bonusRewards cannot be negative");
        }
        if (timeLimit <= 0) {
            throw new IllegalArgumentException("timeLimit must be greater than 0");
        }
        if (enemyCount < 0) {
            throw new IllegalArgumentException("enemyCount cannot be negative");
        }

        this.rows = rows;
        this.cols = cols;
        this.regularRewards = regularRewards;
        this.bonusRewards = bonusRewards;
        this.timeLimit = timeLimit;  // We should change this to turnLimit/tickLimit logic
        this.enemyCount = enemyCount;
        this.trapCount = trapCount;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getRegularRewards() {
        return regularRewards;
    }

    public int getBonusRewards() {
        return bonusRewards;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getEnemyCount() {
        return enemyCount;
    }

    public int getTrapCount() {
        return trapCount;
    }
}
