package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PositionTest {
    @Test
    void constructorStoresRowAndColCorrectly() {
        Position pos = new Position(3, 5);

        assertEquals(3, pos.getRow());
        assertEquals(5, pos.getCol());
    }

    @Test
    void equalsReturnsTrueForSameCoordinates() {
        Position p1 = new Position(2, 4);
        Position p2 = new Position(2, 4);

        assertEquals(p1, p2);
    }

    @Test
    void equalsReturnsTrueForSameObjectReference() {
        Position pos = new Position(7, 8);
        assertEquals(pos, pos);
    }

    //invalid cases
    @Test
    void equalsReturnsFalseForDifferentRow() {
        Position p1 = new Position(2, 4);
        Position p2 = new Position(3, 4);

        assertNotEquals(p1, p2);
    }
    @Test
    void equalsReturnsFalseForDifferentCol() {
        Position p1 = new Position(2, 4);
        Position p2 = new Position(2, 5);

        assertNotEquals(p1, p2);
    }

    @Test
    void equalsReturnsFalseForNull() {
        Position pos = new Position(1, 1);
        assertNotEquals(null, pos);
    }

    @Test
    void equalsReturnsFalseForDifferentType() {
        Position pos = new Position(1, 1);
        assertNotEquals("not a position", pos);
    }

    @Test
    void hashCodeMatchesForEqualObjects() {
        Position p1 = new Position(6, 9);
        Position p2 = new Position(6, 9);

        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void toStringReturnsExpectedFormat() {
        Position pos = new Position(4, 7);
        assertEquals("(4, 7)", pos.toString());
    }

    @Test
    void constructorAllowsNegativeCoordinatesAsStoredValues() {
        Position pos = new Position(-1, -2);

        assertEquals(-1, pos.getRow());
        assertEquals(-2, pos.getCol());
    }
}