package model;

import Item.BonusReward;
import Item.RegularReward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;

    @BeforeEach
    void setUp() {board = new Board();}

    @Test
    void defaultConstructorCreatesBoardWithCorrectDimensions() {
        assertEquals(10, board.getWidth());
        assertEquals(10, board.getHeight());
    }

    @Test
    void defaultConstructorCreatesCorrectStartPosition() {
        assertEquals(new Position(1, 1), board.getStartPos());
        assertEquals(CellType.START, board.getCell(board.getStartPos()).getTitle());
    }

    @Test
    void defaultConstructorCreatesCorrectExitPosition() {
        assertEquals(new Position(8, 8), board.getExitPos());
        assertEquals(CellType.EXIT, board.getCell(board.getExitPos()).getTitle());
    }

    @Test
    void defaultConstructorFillsNormalCellsAsFloor() {
        assertEquals(CellType.FLOOR, board.getCell(new Position(0, 0)).getTitle());
        assertEquals(CellType.FLOOR, board.getCell(new Position(5, 5)).getTitle());
        assertEquals(CellType.FLOOR, board.getCell(new Position(9, 9)).getTitle());
    }

    //load an non-existent map
    @Test
    void loadFromMapThrowsForMissingMap() {
        Board board = new Board();

        assertThrows(IllegalArgumentException.class, () -> board.loadFromMap("definitely_missing_map"));
    }

    @Test
    void loadFromMapThrowsForInconsistentRowWidth() {
        Board board = new Board();

        assertThrows(IllegalArgumentException.class,
                () -> board.loadFromMap("map_inconsistent_width"));
    }

    @Test
    void loadFromMapThrowsForTooSmallMap() {
        Board board = new Board();

        assertThrows(IllegalArgumentException.class,
                () -> board.loadFromMap("map_too_small"));
    }

    @Test
    void loadFromMapThrowsForUnknownCharacter() {
        Board board = new Board();

        assertThrows(IllegalArgumentException.class,
                () -> board.loadFromMap("map_unknown_char"));
    }

    @Test
    void loadFromMapThrowsForMissingStart() {
        Board board = new Board();

        assertThrows(IllegalArgumentException.class,
                () -> board.loadFromMap("map_missing_start"));
    }

    @Test
    void loadFromMapThrowsForMissingExit() {
        Board board = new Board();

        assertThrows(IllegalArgumentException.class,
                () -> board.loadFromMap("map_missing_exit"));
    }

    @Test
    void isInsideReturnsTrueForValidCorners() {
        assertTrue(board.isInside(new Position(0, 0)));
        assertTrue(board.isInside(new Position(9, 9)));
    }

    @Test
    void isInsideReturnsTrueForStartAndExit() {
        assertTrue(board.isInside(board.getStartPos()));
        assertTrue(board.isInside(board.getExitPos()));
    }

    @Test
    void isInsideReturnsFalseForNegativeRow() {
        assertFalse(board.isInside(new Position(-1, 0)));
    }

    @Test
    void isInsideReturnsFalseForNegativeCol() {
        assertFalse(board.isInside(new Position(0, -1)));
    }

    @Test
    void isInsideReturnsFalseForRowTooLarge() {
        assertFalse(board.isInside(new Position(10, 0)));
    }

    @Test
    void isInsideReturnsFalseForColTooLarge() {
        assertFalse(board.isInside(new Position(0, 10)));
    }

    @Test
    void isInsideReturnsFalseForNullPosition() {
        assertFalse(board.isInside(null));
    }

    @Test
    void getCellReturnsCorrectCellForValidPosition() {
        Position pos = new Position(3, 4);
        Cell cell = board.getCell(pos);

        assertEquals(pos, cell.getPos());
        assertEquals(CellType.FLOOR, cell.getTitle());
    }

    @Test
    void getCellThrowsForNullPosition() {
        assertThrows(IllegalArgumentException.class, () -> board.getCell(null));
    }

    @Test
    void getCellThrowsForOutOfBoundsPosition() {
        assertThrows(IllegalArgumentException.class, () -> board.getCell(new Position(100, 100)));
    }

    @Test
    void isWalkableReturnsTrueForFloorCell() {
        assertTrue(board.isWalkable(new Position(0, 0)));
    }

    @Test
    void isWalkableReturnsTrueForStartCell() {
        assertTrue(board.isWalkable(board.getStartPos()));
    }

    @Test
    void isWalkableReturnsTrueForExitCell() {
        assertTrue(board.isWalkable(board.getExitPos()));
    }

    @Test
    void isWalkableReturnsFalseForOutOfBoundsPosition() {
        assertFalse(board.isWalkable(new Position(-1, -1)));
        assertFalse(board.isWalkable(new Position(10, 10)));
    }

    @Test
    void isExitReturnsTrueOnlyForExitPosition() {
        assertTrue(board.isExit(board.getExitPos()));
        assertFalse(board.isExit(board.getStartPos()));
        assertFalse(board.isExit(new Position(0, 0)));
        assertFalse(board.isExit(null));
    }

    @Test
    void placeRegularRewardPlacesItemOnValidWalkableCell() {
        Position pos = new Position(2, 2);

        board.placeRegularReward(pos, 10);

        assertTrue(board.getCell(pos).hasItem());
        assertTrue(board.getCell(pos).getItem() instanceof RegularReward);
    }

    @Test
    void placeBonusRewardPlacesItemOnValidWalkableCell() {
        Position pos = new Position(2, 3);

        board.placeBonusReward(pos, 50, 100, 20);

        assertTrue(board.getCell(pos).hasItem());
        assertTrue(board.getCell(pos).getItem() instanceof BonusReward);
    }

    @Test
    void placeRegularRewardThrowsForNullPosition() {
        assertThrows(IllegalArgumentException.class, () -> board.placeRegularReward(null, 10));
    }

    @Test
    void placeBonusRewardThrowsForNullPosition() {
        assertThrows(IllegalArgumentException.class, () -> board.placeBonusReward(null, 10, 0, 5));
    }

    @Test
    void placeRegularRewardThrowsForOutOfBoundsPosition() {
        assertThrows(IllegalArgumentException.class,
                () -> board.placeRegularReward(new Position(99, 99), 10));
    }

    @Test
    void placeBonusRewardThrowsForOutOfBoundsPosition() {
        assertThrows(IllegalArgumentException.class,
                () -> board.placeBonusReward(new Position(-1, 3), 10, 0, 5));
    }

    @Test
    void placeRegularRewardThrowsWhenPlacingOnStart() {
        assertThrows(IllegalArgumentException.class,
                () -> board.placeRegularReward(board.getStartPos(), 10));
    }

    @Test
    void placeBonusRewardThrowsWhenPlacingOnStart() {
        assertThrows(IllegalArgumentException.class,
                () -> board.placeBonusReward(board.getStartPos(), 10, 0, 5));
    }

    @Test
    void placeRegularRewardThrowsWhenPlacingOnExit() {
        assertThrows(IllegalArgumentException.class,
                () -> board.placeRegularReward(board.getExitPos(), 10));
    }

    @Test
    void placeBonusRewardThrowsWhenPlacingOnExit() {
        assertThrows(IllegalArgumentException.class,
                () -> board.placeBonusReward(board.getExitPos(), 10, 0, 5));
    }

    @Test
    void placeRegularRewardThrowsWhenCellAlreadyHasItem() {
        Position pos = new Position(3, 3);
        board.placeRegularReward(pos, 10);

        assertThrows(IllegalStateException.class, () -> board.placeRegularReward(pos, 20));
    }

    @Test
    void placeBonusRewardThrowsWhenCellAlreadyHasItem() {
        Position pos = new Position(3, 4);
        board.placeRegularReward(pos, 10);

        assertThrows(IllegalStateException.class, () -> board.placeBonusReward(pos, 50, 0, 5));
    }

    @Test
    void removeItemClearsExistingItem() {
        Position pos = new Position(4, 4);
        board.placeRegularReward(pos, 10);

        board.removeItem(pos);

        assertFalse(board.getCell(pos).hasItem());
        assertNull(board.getCell(pos).getItem());
    }

    @Test
    void removeItemOnEmptyValidCellDoesNotCrash() {
        Position pos = new Position(4, 5);

        board.removeItem(pos);

        assertFalse(board.getCell(pos).hasItem());
    }

    @Test
    void removeItemThrowsForInvalidPosition() {
        assertThrows(IllegalArgumentException.class, () -> board.removeItem(new Position(20, 20)));
    }

    @Test
    void countRegularRemainingReturnsZeroInitially() {
        assertEquals(0, board.countRegularRemaining());
    }

    @Test
    void countRegularRemainingCountsOnlyRegularRewards() {
        board.placeRegularReward(new Position(2, 2), 10);
        board.placeRegularReward(new Position(2, 4), 20);
        board.placeBonusReward(new Position(2, 5), 50, 0, 10);

        assertEquals(2, board.countRegularRemaining());
    }

    @Test
    void countRegularRemainingDecreasesAfterRemovingRegularReward() {
        Position pos1 = new Position(2, 2);
        Position pos2 = new Position(2, 4);

        board.placeRegularReward(pos1, 10);
        board.placeRegularReward(pos2, 20);
        assertEquals(2, board.countRegularRemaining());

        board.removeItem(pos1);

        assertEquals(1, board.countRegularRemaining());
    }

    @Test
    void updatingOneCellDoesNotChangeOtherCells() {
        Position changed = new Position(5, 5);
        Position untouched = new Position(5, 6);

        board.placeRegularReward(changed, 10);

        assertTrue(board.getCell(changed).hasItem());
        assertFalse(board.getCell(untouched).hasItem());
        assertEquals(CellType.FLOOR, board.getCell(untouched).getTitle());
    }

    @Test
    void startAndExitCellsRemainSpecialAfterOtherCellsAreModified() {
        board.placeRegularReward(new Position(6, 6), 10);

        assertEquals(CellType.START, board.getCell(board.getStartPos()).getTitle());
        assertEquals(CellType.EXIT, board.getCell(board.getExitPos()).getTitle());
    }
}