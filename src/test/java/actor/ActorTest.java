package actor;

import model.Board;
import model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActorTest {

    private static class TestActor extends Actor {
        TestActor(Position startPos, Board board) {
            super(startPos, board);
        }

        Position next(Position current, Direction dir) {
            return nextPosition(current, dir);
        }
    }

    private Board board() {
        Board board = new Board();
        board.loadFromMap("test_31x15");
        return board;
    }

    @Test
    void testStartCell() {
        Board board = board();
        Position start = board.getStartPos();

        TestActor actor = new TestActor(start, board);

        assertEquals(start, actor.getPosition());
        assertSame(board, actor.getBoard());
        assertSame(actor, board.getCell(start).getOccupant());
    }

    @Test
    void testOccupiedStart() {
        Board board = board();
        Position start = board.getStartPos();
        new TestActor(start, board);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new TestActor(start, board)
        );

        assertEquals("Starting position is already occupied by another actor", error.getMessage());
    }

    @Test
    void testSetPos() {
        Board board = board();
        Position start = board.getStartPos();
        Position end = new Position(1, 2);
        TestActor actor = new TestActor(start, board);

        actor.setPosition(end);

        assertEquals(end, actor.getPosition());
        assertNull(board.getCell(start).getOccupant());
        assertSame(actor, board.getCell(end).getOccupant());
    }

    @Test
    void testSamePos() {
        Board board = board();
        Position start = board.getStartPos();
        TestActor actor = new TestActor(start, board);

        actor.setPosition(start);

        assertEquals(start, actor.getPosition());
        assertSame(actor, board.getCell(start).getOccupant());
    }

    @Test
    void testTryMove() {
        Board board = board();
        Position start = board.getStartPos();
        Position end = new Position(1, 2);
        TestActor actor = new TestActor(start, board);

        assertTrue(actor.tryMove(Direction.RIGHT));

        assertEquals(end, actor.getPosition());
        assertNull(board.getCell(start).getOccupant());
        assertSame(actor, board.getCell(end).getOccupant());
    }

    @Test
    void testBlockedMove() {
        Board board = board();
        Position start = board.getStartPos();
        TestActor actor = new TestActor(start, board);

        assertFalse(actor.tryMove(Direction.LEFT));
        assertFalse(actor.tryMove(Direction.NONE));
        assertFalse(actor.tryMove(null));
        assertEquals(start, actor.getPosition());
        assertSame(actor, board.getCell(start).getOccupant());
    }

    @Test
    void testOccupiedPos() {
        Board board = board();
        TestActor actor = new TestActor(board.getStartPos(), board);
        Position occupied = new Position(1, 2);
        new TestActor(occupied, board);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> actor.setPosition(occupied)
        );

        assertEquals("Target position is occupied by another actor", error.getMessage());
    }

    @Test
    void testBadPos() {
        Board board = board();
        TestActor actor = new TestActor(board.getStartPos(), board);

        assertThrows(IllegalArgumentException.class, () -> actor.setPosition(null));
        assertThrows(IllegalArgumentException.class, () -> actor.setPosition(new Position(-1, 0)));
        assertThrows(IllegalArgumentException.class, () -> actor.setPosition(new Position(0, 0)));
    }

    @Test
    void testNextPos() {
        TestActor actor = new TestActor(new Position(1, 1), board());
        Position current = new Position(5, 5);

        assertEquals(new Position(4, 5), actor.next(current, Direction.UP));
        assertEquals(new Position(6, 5), actor.next(current, Direction.DOWN));
        assertEquals(new Position(5, 4), actor.next(current, Direction.LEFT));
        assertEquals(new Position(5, 6), actor.next(current, Direction.RIGHT));
        assertNull(actor.next(current, Direction.NONE));
        assertNull(actor.next(current, null));
        assertNull(actor.next(null, Direction.UP));
    }
}
