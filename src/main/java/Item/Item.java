package Item;
import engine.GameState;

/**
 * Abstract class getting inherited for all Classes of type "Item"
 * in game.
 */

public abstract class Item {

    protected int value;

    public Item(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public abstract void onCollected(GameState state);
}
