package engine;

public class GameState {
    // ---- Progress / status ----
    private Status status;                  // RUNNING / WON / LOST / etc.
    private int time;                       // time
    private int elapsedTicks;               // number of ticks passed
    private int stepUsed;                   // number of player steps used
    private int regularRewardsRemaining;    // remaining required collectibles

    private final ScoreCount scoreCount;


    /**
     * Create a new engine.GameState for a level run.
     *
     * @param initialRegularRewardsRemaining required collectibles to finish the level
     * @param initialTime initial time value
     */
    public GameState(int initialRegularRewardsRemaining, int initialTime) {
        if (initialRegularRewardsRemaining < 0) {
            throw new IllegalArgumentException("initialRegularRewardsRemaining cannot be negative");
        }
        if (initialTime < 0) {
            throw new IllegalArgumentException("initialTime cannot be negative");
        }

        this.status = Status.PREVIEW; // or RUNNING, depending on your flow
        this.time = initialTime;
        this.elapsedTicks = 0;
        this.stepUsed = 0;
        this.regularRewardsRemaining = initialRegularRewardsRemaining;

        this.scoreCount = new ScoreCount();
        this.scoreCount.computeTotal();

    }

    // ----------------------------
    // Getters
    // ----------------------------
    public Status getStatus() {
        return status;
    }

    public int getTime() {
        return time;
    }

    public int getElapsedTicks() {
        return elapsedTicks;
    }

    public int getStepUsed() {
        return stepUsed;
    }

    public int getRegularRewardsRemaining() {
        return regularRewardsRemaining;
    }

    public ScoreCount getScoreCount() {
        return scoreCount;
    }


    // ----------------------------
    // engine.Status / win-lose
    // ----------------------------

    /**
     * - engine.GameEngine decides WIN and calls setStatus(WON).
     */
/*    public boolean isWin() {     /// I do not think this is necessary, as engine.GameEngine already has checkWin()
        return status == engine.Status.RUNNING && regularRewardsRemaining == 0;
    }
*/
    /**
     * - engine.GameEngine decides LOSE and calls setStatus(LOST).
     */
    public boolean isLose() {
        return status == Status.LOST;
    }

    // ----------------------------
    // Progress updates (engine.GameEngine calls)
    // ----------------------------

    /**
     * Update time and ticks.
     * "elapsed tick": +1 each tick.
     * "time": -1 each tick.
     */

    public void setStatus(Status status) {
        if (status == null) throw new IllegalArgumentException("status cannot be null");
        this.status = status;
    }

    public void updateTime() {
        int delta = 1;
//        time = time - delta;
//        if (time < 0) time = 0;
//
//        elapsedTicks += 1;
        time = Math.max(0, time - delta);
        elapsedTicks += 1;
    }

    /** Call when the player successfully spends a step (one move/action). */
    public void incrementStepUsed() {
        stepUsed += 1;
    }

    /** Call when a required (regular) reward is collected. */
    public void decreaseRegularRewards() {
        if (regularRewardsRemaining > 0) {
            regularRewardsRemaining -= 1;
        }
    }

    // ----------------------------
    // Score updates (Items call in onCollected)
    // ----------------------------
    public void addCollection(int delta) {
        scoreCount.addCollection(delta);
        scoreCount.computeTotal();
    }

    public void addBonus(int delta) {
        scoreCount.addBonus(delta);
        scoreCount.computeTotal();
    }

    public void addTrapKillBonus() {
        addBonus(50);
    }

    public void addPenalty(int delta) {
        scoreCount.addPenalty(delta);
        scoreCount.computeTotal();
    }

    public void setTimeScore(int value) {
        scoreCount.setTimeScore(value);
        scoreCount.computeTotal();
    }
}
