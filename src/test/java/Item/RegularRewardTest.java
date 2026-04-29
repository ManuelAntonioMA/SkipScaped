package Item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import engine.*;
import Item.*;

class RegularRewardTest {

     @Test
    void testOnCollected() {
        GameState state = new GameState(5, 0);
        RegularReward reward = new RegularReward(5);

        reward.onCollected(state);

        ScoreCount counter = state.getScoreCount();

        assertEquals(5, counter.getTotalScore());

        assertEquals(4, state.getRegularRewardsRemaining());
    }
    @Test
    void testZeroValueReward() {
        GameState state = new GameState(1, 0); 
        RegularReward zeroReward = new RegularReward(0);

        zeroReward.onCollected(state);

        assertEquals(0, state.getScoreCount().getTotalScore());
        assertEquals(0, state.getRegularRewardsRemaining());
    }
    @Test
    void testCollectingLastReward() {
        GameState state = new GameState(1, 0); // Only 1 reward exists
        RegularReward reward = new RegularReward(5);

        reward.onCollected(state);

        assertEquals(0, state.getRegularRewardsRemaining());
        // If your game has a 'isLevelComplete()' method, check it here!
    }
    @Test
    void testMultipleCollectionsAccumulate() {
        GameState state = new GameState(10, 0);
        RegularReward r1 = new RegularReward(5);
        RegularReward r2 = new RegularReward(10);

        r1.onCollected(state);
        r2.onCollected(state);

        assertEquals(15, state.getScoreCount().getTotalScore());
        assertEquals(8, state.getRegularRewardsRemaining());
    }
    @Test
    void testItemGetValue() {
        RegularReward reward = new RegularReward(100);
        assertEquals(100, reward.getValue(), "The getValue method inherited from Item should return the correct amount");
    }
}