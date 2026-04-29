package engine;

public class ScoreCount {

    private int collectionScore;
    private int timeScore;
    private int bonusScore;
    private int penaltyScore;
    private int totalScore;

    //---------
    // Getter
    //---------
    public int getCollectionScore() {
        return collectionScore;
    }
    public int getBonusScore() {
        return bonusScore;
    }
    public int getTimeScore() {
        return timeScore;
    }
    public int getPenaltyScore() {
        return penaltyScore;
    }
    public int getTotalScore() {
        return totalScore;
    }

    public int computeTotal() {
        totalScore = collectionScore + bonusScore + timeScore - penaltyScore;
        return totalScore;
    }

    //-----------------
    //call in gameState
    //----------------
    void addCollection(int delta) { collectionScore += delta; }
    void addBonus(int delta) { bonusScore += delta; }
    void addPenalty(int delta) { penaltyScore += delta; }
    void setTimeScore(int value) { timeScore = value; }
}
