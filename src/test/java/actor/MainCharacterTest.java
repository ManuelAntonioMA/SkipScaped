package actor;

import model.Board;
import model.Position;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainCharacterTest {

    private Board board() {
        Board board = new Board();
        board.loadFromMap("test_31x15");
        return board;
    }

    @Test
    void testMoveHistory() {
        Board board = board();
        Position start = board.getStartPos();
        MainCharacter player = new MainCharacter(start, board);

        player.handleInput(Direction.RIGHT);

        assertEquals(new Position(1, 2), player.getPosition());
        assertEquals(List.of(start, new Position(1, 2)), new ArrayList<>(player.getPathHistory()));
    }

    @Test
    void testNoMoveHistory() {
        Board board = board();
        Position start = board.getStartPos();
        MainCharacter player = new MainCharacter(start, board);

        player.handleInput(Direction.LEFT);

        assertEquals(start, player.getPosition());
        assertEquals(List.of(start), new ArrayList<>(player.getPathHistory()));
    }

    @Test
    void testTrimHistory() {
        Board board = board();
        MainCharacter player = new MainCharacter(board.getStartPos(), board);

        player.handleInput(Direction.RIGHT);
        player.handleInput(Direction.RIGHT);
        player.setMaxHistorySize(2);

        assertEquals(
                List.of(new Position(1, 2), new Position(1, 3)),
                new ArrayList<>(player.getPathHistory())
        );
    }

    @Test
    void testOverMaxHistory() {
        Board board = board();
        MainCharacter player = new MainCharacter(board.getStartPos(), board);

        player.setMaxHistorySize(1);
        player.handleInput(Direction.RIGHT);

        assertEquals(List.of(new Position(1, 2)), new ArrayList<>(player.getPathHistory()));
    }

    @Test
    void testBadMaxHistory() {
        Board board = board();
        MainCharacter player = new MainCharacter(board.getStartPos(), board);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> player.setMaxHistorySize(0)
        );

        assertEquals("Max history size must be at least 1", error.getMessage());
    }

    @Test
    void testCopyHistory() {
        Board board = board();
        MainCharacter player = new MainCharacter(board.getStartPos(), board);
        ArrayDeque<Position> copy = new ArrayDeque<>(player.getPathHistory());

        copy.clear();

        assertEquals(List.of(board.getStartPos()), new ArrayList<>(player.getPathHistory()));
    }

    @Test
    void testTraps() {
        Board board = board();
        MainCharacter player = new MainCharacter(board.getStartPos(), board);

        assertFalse(player.useTrap());

        player.addTrap();
        player.addTrap();

        assertEquals(2, player.getTrapInventory());
        assertTrue(player.useTrap());
        assertEquals(1, player.getTrapInventory());
    }
}

