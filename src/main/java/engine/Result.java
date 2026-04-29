package engine;

/**
 * Class that builds the results after GameOver
 */
public class Result {
    private Status status;
    private int time;
    private ScoreCount scoreCount;
    private LoseReason lossReason;

    public Result(Status status, int time, ScoreCount scoreCount, LoseReason lossReason) {
        this.status = status;
        this.time = time;
        this.scoreCount = scoreCount;
        this.lossReason = lossReason;
    }

    public boolean isWin() { return status == Status.WON; }
    public boolean isLose() { return status == Status.LOST; }

    public Status getStatus() {
        return status;
    }

    public int getTime() {
        return time;
    }

    public ScoreCount getScoreCount() {
        return scoreCount;
    }
    
    public LoseReason getLoseReason() {
        return lossReason;
    }
}
