package Item;
import engine.GameState;

public class RegularReward extends Reward {

    public RegularReward(int amount) {
        super(amount);
    }

    @Override
    public void onCollected(GameState state) {
        state.addCollection(getValue());
        state.decreaseRegularRewards();
    }
}
