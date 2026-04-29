package Item;
import engine.GameState;

/**
 * 
 * This class represents Bonus Rewards that can be picked
 * up on the Booard to eventually increase the score.
 * 
 */

public class BonusReward extends Reward {

    private int ttlTicks;
    private int spawnTick;

    public BonusReward(int amount, int ttlTicks, int spawnTick) {
        super(amount);
        this.ttlTicks = ttlTicks;
        this.spawnTick = spawnTick;
    }

    public boolean isExpired(int currentTick) {
        return (currentTick - spawnTick) >= ttlTicks;
    }

    @Override
    public void onCollected(GameState state) {
        state.addBonus(getValue());
    }
}
