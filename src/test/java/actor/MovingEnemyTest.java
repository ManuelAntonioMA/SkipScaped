package actor;

import model.Board;
import model.Cell;
import model.CellType;
import model.Position;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MovingEnemyTest {

    private static class TestEnemy extends MovingEnemy {
        TestEnemy(Position startPos, Board board, int speedFactor) {
            super(startPos, board, speedFactor);
        }

        void forcePos(Position pos) {
            setPositionDirect(pos);
        }
    }

    private static class SplitBoard extends Board {
        private final Map<Position, Cell> cells = new HashMap<>();

        SplitBoard() {
            addWalkable(new Position(1, 1), CellType.START);
            addWalkable(new Position(1, 2), CellType.FLOOR);
            addWalkable(new Position(1, 3), CellType.FLOOR);
            addWalkable(new Position(1, 4), CellType.FLOOR);
            addWalkable(new Position(1, 5), CellType.EXIT);

            addWalkable(new Position(3, 1), CellType.FLOOR);
            addWalkable(new Position(3, 2), CellType.FLOOR);
            addWalkable(new Position(3, 3), CellType.FLOOR);
        }

        private void addWalkable(Position pos, CellType type) {
            cells.put(pos, new Cell(pos, type));
        }

        @Override
        public boolean isInside(Position pos) {
            return pos != null && cells.containsKey(pos);
        }

        @Override
        public boolean isWalkable(Position pos) {
            return pos != null && cells.containsKey(pos);
        }

        @Override
        public Cell getCell(Position pos) {
            if (!isInside(pos)) {
                throw new IllegalArgumentException("Position out of bounds: " + pos);
            }
            return cells.get(pos);
        }

        @Override
        public Position getStartPos() {
            return new Position(1, 1);
        }
    }

    private Board board() {
        Board board = new Board();
        board.loadFromMap("test_31x15");
        return board;
    }

    @Test
    void testBadSpeedFactor() {
        Board board = board();

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new MovingEnemy(new Position(3, 1), board, 0)
        );

        assertEquals("Speed factor must be >= 1", error.getMessage());
    }

    @Test
    void testSetSpeed() {
        MovingEnemy enemy = new MovingEnemy(new Position(3, 1), board(), 2);

        assertEquals(2, enemy.getSpeedFactor());
        enemy.setSpeedFactor(3);
        assertEquals(3, enemy.getSpeedFactor());
    }

    @Test
    void testBadSetSpeed() {
        MovingEnemy enemy = new MovingEnemy(new Position(3, 1), board(), 1);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> enemy.setSpeedFactor(0)
        );

        assertEquals("Speed factor must be >= 1", error.getMessage());
    }

    @Test
    void testChasePlayer() {
        Board board = board();
        MainCharacter player = new MainCharacter(board.getStartPos(), board);
        Position start = new Position(3, 1);
        Position next = new Position(2, 1);
        MovingEnemy enemy = new MovingEnemy(start, board, 1);

        enemy.update(player);

        assertEquals(next, enemy.getPosition());
        assertNull(board.getCell(start).getOccupant());
        assertSame(enemy, board.getCell(next).getOccupant());
    }

    @Test
    void testWaitTickBeforeChase() {
        Board board = board();
        MainCharacter player = new MainCharacter(board.getStartPos(), board);
        MovingEnemy enemy = new MovingEnemy(new Position(3, 1), board, 2);

        enemy.update(player);
        assertEquals(new Position(3, 1), enemy.getPosition());

        enemy.update(player);
        assertEquals(new Position(2, 1), enemy.getPosition());
    }

    @Test
    void testNoPlayer() {
        Position start = new Position(3, 1);
        MovingEnemy enemy = new MovingEnemy(start, board(), 1);

        enemy.update(null);

        assertEquals(start, enemy.getPosition());
    }

    @Test
    void testSameCellWithPlayer() {
        Board board = board();
        MainCharacter player = new MainCharacter(board.getStartPos(), board);
        TestEnemy enemy = new TestEnemy(new Position(2, 1), board, 1);

        enemy.forcePos(player.getPosition());
        enemy.update(player);

        assertEquals(player.getPosition(), enemy.getPosition());
    }

    @Test
    void testNoPathToMove() {
        Board board = new SplitBoard();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        MovingEnemy enemy = new MovingEnemy(new Position(3, 1), board, 1);

        enemy.update(player);

        assertEquals(new Position(3, 1), enemy.getPosition());
        assertSame(enemy, board.getCell(new Position(3, 1)).getOccupant());
    }

    @Test
    void testMoveToPlayerCell() {
        Board board = board();
        MainCharacter player = new MainCharacter(board.getStartPos(), board);
        MovingEnemy enemy = new MovingEnemy(new Position(2, 1), board, 1);

        enemy.update(player);

        assertEquals(player.getPosition(), enemy.getPosition());
        assertSame(player, board.getCell(player.getPosition()).getOccupant());
    }
}
