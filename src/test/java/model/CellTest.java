package model;

import Item.RegularReward;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    @Test
    void constructorStoresPositionAndType() {
        Position pos = new Position(2, 3);
        Cell cell = new Cell(pos, CellType.FLOOR);

        assertEquals(pos, cell.getPos());
        assertEquals(CellType.FLOOR, cell.getTitle());
    }

    @Test
    void constructorThrowsWhenPositionIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Cell(null, CellType.FLOOR)
        );

        assertTrue(ex.getMessage().contains("Position"));
    }

    @Test
    void constructorThrowsWhenCellTypeIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Cell(new Position(0, 0), null)
        );

        assertTrue(ex.getMessage().contains("CellType"));
    }

    @Test
    void newCellStartsWithNoItem() {
        Cell cell = new Cell(new Position(1, 1), CellType.FLOOR);

        assertFalse(cell.hasItem());
        assertNull(cell.getItem());
    }

    @Test
    void setItemMakesCellContainItem() {
        Cell cell = new Cell(new Position(1, 1), CellType.FLOOR);
        RegularReward reward = new RegularReward(10);

        cell.setItem(reward);

        assertTrue(cell.hasItem());
        assertEquals(reward, cell.getItem());
    }

    @Test
    void clearItemRemovesExistingItem() {
        Cell cell = new Cell(new Position(1, 1), CellType.FLOOR);
        cell.setItem(new RegularReward(10));

        cell.clearItem();

        assertFalse(cell.hasItem());
        assertNull(cell.getItem());
    }

    @Test
    void setItemNullKeepsCellEmpty() {
        Cell cell = new Cell(new Position(1, 1), CellType.FLOOR);

        cell.setItem(null);

        assertFalse(cell.hasItem());
        assertNull(cell.getItem());
    }

    @Test
    void newCellStartsWithNoOccupant() {
        Cell cell = new Cell(new Position(1, 1), CellType.FLOOR);

        assertFalse(cell.hasOccupant());
        assertNull(cell.getOccupant());
    }

    @Test
    void clearOccupantOnEmptyCellKeepsItEmpty() {
        Cell cell = new Cell(new Position(1, 1), CellType.FLOOR);

        cell.clearOccupant();

        assertFalse(cell.hasOccupant());
        assertNull(cell.getOccupant());
    }

    @Test
    void setOccupantNullKeepsCellEmpty() {
        Cell cell = new Cell(new Position(1, 1), CellType.FLOOR);

        cell.setOccupant(null);

        assertFalse(cell.hasOccupant());
        assertNull(cell.getOccupant());
    }

    @Test
    void changingItemDoesNotChangePositionOrType() {
        Position pos = new Position(4, 5);
        Cell cell = new Cell(pos, CellType.START);

        cell.setItem(new RegularReward(20));
        cell.clearItem();

        assertEquals(pos, cell.getPos());
        assertEquals(CellType.START, cell.getTitle());
    }

    @Test
    void clearingOccupantDoesNotChangePositionOrType() {
        Position pos = new Position(6, 7);
        Cell cell = new Cell(pos, CellType.EXIT);

        cell.clearOccupant();

        assertEquals(pos, cell.getPos());
        assertEquals(CellType.EXIT, cell.getTitle());
    }
}