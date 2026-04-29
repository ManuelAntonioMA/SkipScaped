package Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import engine.GameState;

class BonusRewardTest {

    @Test
    void testNotExpired() {
        BonusReward reward = new BonusReward(10, 5, 0);
        assertFalse(reward.isExpired(4));
    }

    @Test
    void testExpiredExactlyAtTTL() {
        BonusReward reward = new BonusReward(10, 5, 0);
        assertTrue(reward.isExpired(5));
    }

    @Test
    void testExpiredAfterTTL() {
        BonusReward reward = new BonusReward(10, 5, 0);
        assertTrue(reward.isExpired(10));
    }

    @Test
    void testOnCollectedAddsBonus() {
        GameState state = new GameState(10,0);
        BonusReward reward = new BonusReward(10, 5, 0);

        reward.onCollected(state);

        assertEquals(10, state.getScoreCount().getTotalScore());
    }

    @Test
    public void testBonusRewardExpiration() {
    // spawnTick = 10, ttl = 5. Should expire at tick 15 or later.
        BonusReward bonus = new BonusReward(100, 5, 10);

        assertFalse(bonus.isExpired(14), "Should be expired exactly at tick 14");
        assertTrue(bonus.isExpired(15), "Should be expired exactly at tick 15");
        assertTrue(bonus.isExpired(20), "Should be expired well after tick 15");
    }
    @Test
    void testCurrentTickBeforeSpawnTick() {
        // Spawned at 10, TTL is 5. What if we check at tick 5?
        BonusReward reward = new BonusReward(10, 5, 10);
        assertFalse(reward.isExpired(5), "Should not be expired if current time is before spawn time");
    }
    @Test
    void testZeroTTL() {
        // Amount 10, TTL 0, Spawned at 0.
        BonusReward reward = new BonusReward(10, 0, 0);
        assertTrue(reward.isExpired(0), "Reward with 0 TTL should be expired immediately at spawn tick");
    }
    @Test
    void testVeryLargeTick() {
        BonusReward reward = new BonusReward(10, 5, 0);
        assertTrue(reward.isExpired(Integer.MAX_VALUE));
    }
}
