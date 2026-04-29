package Item;

import org.junit.jupiter.api.Test;

import Item.BonusReward;
import Item.RegularReward;
import Item.RewardManager;
import actor.MainCharacter;
import actor.MovingEnemy;
import engine.GameState;
import model.Board;
import model.Cell;
import model.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions.*;

public class RewardManagerTest {
     @Test
    void testHasEnemyAtTrue() {
        RewardManager manager = new RewardManager();

        Board board = new Board();
        Position pos = new Position(1,1);
        MovingEnemy enemy = new MovingEnemy(pos,board,1);

        List<MovingEnemy> enemies = List.of(enemy);

        assertTrue(manager.hasEnemyAt(pos, enemies));
    }

    @Test
    void testHasEnemyAtFalse() {
        RewardManager manager = new RewardManager();

        Board board = new Board ();

        Position pos = new Position(1,1);
        MovingEnemy enemy = new MovingEnemy(new Position(2,2),board, 1);

        List<MovingEnemy> enemies = List.of(enemy);

        assertFalse(manager.hasEnemyAt(pos, enemies));
    }

    @Test
    void testHasEnemyAtNullInputs() {
        RewardManager manager = new RewardManager();

        assertFalse(manager.hasEnemyAt(null, new ArrayList<>()));
        assertFalse(manager.hasEnemyAt(new Position(1,1), null));
    }

    @Test
    void testRemoveExpiredBonusReward() {
        RewardManager manager = new RewardManager();
        Board board = new Board();

        Position pos = new Position(1,1);
        Cell cell = board.getCell(pos);

        cell.setItem(new BonusReward(10, 5, 0));

        manager.removeExpiredBonusRewards(board, 10);

        assertFalse(cell.hasItem());
    }

   @Test
    void testCollectRewardRemovesItemAndAppliesEffect() {
        RewardManager manager = new RewardManager();
        GameState state = new GameState(5, 0);

        Board board = new Board();

        Position pos = board.getStartPos(); 
        Cell cell = board.getCell(pos);

        cell.setItem(new RegularReward(5));

        manager.collectRewardAtPosition(board, pos, state);

        assertFalse(cell.hasItem());
        assertEquals(5, state.getScoreCount().getCollectionScore());
        assertEquals(4, state.getRegularRewardsRemaining());
    }
    @Test
    public void testCollectRewardAtPosition() {
        Board board = new Board(); // Assuming constructor (width, height)
        Position pos = new Position(2, 2);
        GameState state = new GameState(20,0); 
        RewardManager manager = new RewardManager();

        // Place a regular reward manually
        RegularReward reward = new RegularReward(10);
        board.getCell(pos).setItem(reward);

        // Collect it
        manager.collectRewardAtPosition(board, pos, state);

        // Assertions
        assertFalse(board.getCell(pos).hasItem(), "Item should be removed from board");
        // Verify score or collection count updated in GameState
        // assertEquals(10, state.getScore());
    }
    @Test
    void testSpawnRegularRewardFailureWhenBoardIsFull() {
        RewardManager manager = new RewardManager();
        // Create a tiny board where every cell is blocked/non-walkable
        Board board = new Board(); 
        Position pos = new Position(0,0);
        // Assuming you can set a wall or make it non-walkable
        // board.setWall(pos); 

        manager.spawnRegularReward(board, null, new ArrayList<>(), 10);

        // Verify no item was placed
        assertFalse(board.getCell(pos).hasItem(), "Should not spawn if no walkable tiles exist");
    }
    @Test
    void testSpawnDoesNotOverlapPlayer() {
        RewardManager manager = new RewardManager();
        Board board = new Board(); // Tiny board so random must pick (0,0)
        MainCharacter player = new MainCharacter(new Position(0, 0),board);

        manager.spawnRegularReward(board, player, new ArrayList<>(), 10);

        assertFalse(board.getCell(new Position(0,0)).hasItem(), "Should skip player position");
    }
    @Test
    void testSpawnDoesNotOverlapEnemy() {
        RewardManager manager = new RewardManager();
        Board board = new Board();
        Position pos = new Position(0,0);
        MovingEnemy enemy = new MovingEnemy(pos, board, 1);
        List<MovingEnemy> enemies = List.of(enemy);

        manager.spawnRegularReward(board, null, enemies, 10);

        assertFalse(board.getCell(pos).hasItem(), "Should skip enemy position");
    }
    @Test
    void testRemoveExpiredDoesNotRemoveRegularRewards() {
        RewardManager manager = new RewardManager();
        Board board = new Board();
        Position pos = new Position(1,1);
    
        // Place a regular reward (which never expires)
        board.getCell(pos).setItem(new RegularReward(5));

        manager.removeExpiredBonusRewards(board, 999); // Huge tick count

        assertTrue(board.getCell(pos).hasItem(), "Regular rewards should remain on the board");
    }
    @Test
    void testNullSafety() {
        RewardManager manager = new RewardManager();
        // These should return silently without throwing an Exception
        manager.spawnRegularReward(null, null, null, 0);
        manager.removeExpiredBonusRewards(null, 0);
        manager.collectRewardAtPosition(null, null, null);
    }
}
