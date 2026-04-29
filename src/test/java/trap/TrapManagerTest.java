package trap;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import actor.*;
import model.Board;
import model.Position;

import static org.junit.jupiter.api.Assertions.*;

public class TrapManagerTest {
   @Test
    void testPlaceTrapAtPlayer() {
        Board board = new Board();
        Position pos = new Position(1, 1);
        MainCharacter player = new MainCharacter(pos, board);

        TrapManager manager = new TrapManager();

        player.addTrap(); // give player 1 trap

        manager.placeTrapAtPlayer(player);

        assertEquals(1, manager.getTraps().size());

        Trap trap = manager.getTraps().get(0);
        assertTrue(trap.isArmed());
        assertEquals(pos, trap.getPosition());
    }

    @Test
    void placeTrapAtPlayer_whenTrapAlreadyExists_doesNotAddSecondTrap() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        TrapManager manager = new TrapManager();

        player.addTrap();
        player.addTrap();

        manager.placeTrapAtPlayer(player);
        manager.placeTrapAtPlayer(player);

        assertEquals(1, manager.getTraps().size());
    }

    @Test
    void testPlaceTrapFailsWithoutInventory() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);

        TrapManager manager = new TrapManager();

        manager.placeTrapAtPlayer(player);

        assertEquals(0, manager.getTraps().size());
    }

    @Test
    void testTrapIgnoredUntilLeave() {
        Board board = new Board();
        Position pos = new Position(1, 1);
        MainCharacter player = new MainCharacter(pos, board);

        TrapManager manager = new TrapManager();

        player.addTrap();
        manager.placeTrapAtPlayer(player);

        // Player is still standing on trap
        boolean triggered = manager.playerTriggeredTrap(player);

        assertFalse(triggered);
    }

    @Test
    void testPlayerTriggersTrapAfterLeaving() {
        Board board = new Board();
        Position pos = new Position(1, 1);
        MainCharacter player = new MainCharacter(pos, board);

        TrapManager manager = new TrapManager();

        player.addTrap();
        manager.placeTrapAtPlayer(player);

        // Move player off trap
        player.setPosition(new Position(2, 2));
        manager.updatePlacedTrapSafety(player);

        // Move back onto trap
        player.setPosition(pos);

        boolean triggered = manager.playerTriggeredTrap(player);

        assertTrue(triggered);
    }

    @Test
    void testTrapPickup() {
        Board board = new Board();
        Position pos = new Position(1, 1);
        MainCharacter player = new MainCharacter(pos, board);

        TrapManager manager = new TrapManager();

        // Manually add a pickup trap (we can't use getTraps().add)
        manager.spawnTrapPickup(board, null, null);

        // Force player onto trap position
        Trap trap = manager.getTraps().get(0);
        player.setPosition(trap.getPosition());

        manager.checkTrapPickup(player);

        assertEquals(1, player.getTrapInventory());
    }


    @Test
    void checkEnemyTrapCollisions_withNullList_returnsFalse() {
        TrapManager manager = new TrapManager();
        assertFalse(manager.checkEnemyTrapCollisions(null));
    }


    @Test
    void checkEnemyTrapCollisions_withNullEnemyInList_doesNotCrash() {
        TrapManager manager = new TrapManager();
        List<MovingEnemy> enemies = new ArrayList<>();
        enemies.add(null);

        assertFalse(manager.checkEnemyTrapCollisions(enemies));
    }

    @Test
    void checkEnemyTrapCollisions_whenNoEnemyOnTrap_returnsFalse() {
        Board board = new Board();
        TrapManager manager = new TrapManager();

        MovingEnemy enemy = new MovingEnemy(new Position(5, 5), board, 1);
        List<MovingEnemy> enemies = new ArrayList<>();
        enemies.add(enemy);

        assertFalse(manager.checkEnemyTrapCollisions(enemies));
        assertEquals(1, enemies.size());
    }

    @Test
    void hasEnemyAt_withNullPosition_returnsFalse() {
        TrapManager manager = new TrapManager();
        List<MovingEnemy> enemies = new ArrayList<>();
        assertFalse(manager.hasEnemyAt(null, enemies));
    }


    @Test
    void hasEnemyAt_whenEnemyExistsAtPosition_returnsTrue() {
        Board board = new Board();
        TrapManager manager = new TrapManager();

        MovingEnemy enemy = new MovingEnemy(new Position(2, 2), board, 1);
        List<MovingEnemy> enemies = new ArrayList<>();
        enemies.add(enemy);

        assertTrue(manager.hasEnemyAt(new Position(2, 2), enemies));
    }

    @Test
    void hasEnemyAt_whenEnemyDoesNotExistAtPosition_returnsFalse() {
        Board board = new Board();
        TrapManager manager = new TrapManager();

        MovingEnemy enemy = new MovingEnemy(new Position(2, 2), board, 1);
        List<MovingEnemy> enemies = new ArrayList<>();
        enemies.add(enemy);

        assertFalse(manager.hasEnemyAt(new Position(3, 3), enemies));
    }

    @Test
    void checkEnemyTrapCollisions_whenEnemyOnArmedTrap_killsEnemyAndDeactivatesTrap() {
        Board board = new Board();
        TrapManager manager = new TrapManager();

        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        player.addTrap();
        manager.placeTrapAtPlayer(player);

        player.setPosition(new Position(1, 2));
        manager.updatePlacedTrapSafety(player);

        MovingEnemy enemy = new MovingEnemy(new Position(1, 1), board, 1);
        board.getCell(new Position(1, 1)).setOccupant(enemy);

        List<MovingEnemy> enemies = new ArrayList<>();
        enemies.add(enemy);

        boolean result = manager.checkEnemyTrapCollisions(enemies);

        assertTrue(result);
        assertEquals(0, enemies.size());
        assertFalse(manager.getTraps().get(0).isActive());
        assertNull(board.getCell(new Position(1, 1)).getOccupant());
    }

    @Test
    void checkEnemyTrapCollisions_whenEnemyPositionIsNull_skipsEnemy() {
        Board board = new Board();
        TrapManager manager = new TrapManager();

        TestEnemy enemy = new TestEnemy(new Position(2, 2), board, 1);
        enemy.forcePos(null);

        List<MovingEnemy> enemies = new ArrayList<>();
        enemies.add(enemy);

        assertFalse(manager.checkEnemyTrapCollisions(enemies));
        assertEquals(1, enemies.size());
    }

    private static class TestEnemy extends MovingEnemy { // Helper
        TestEnemy(Position startPos, Board board, int speedFactor) {
            super(startPos, board, speedFactor);
        }

        void forcePos(Position pos) {
            setPositionDirect(pos);
        }
    }

    @Test
    void hasEnemyAt_withNullEnemyInsideList_returnsFalse() {
        TrapManager manager = new TrapManager();
        List<MovingEnemy> enemies = new ArrayList<>();
        enemies.add(null);

        assertFalse(manager.hasEnemyAt(new Position(2, 2), enemies));
    }
}
