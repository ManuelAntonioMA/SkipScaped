package trap;
import org.junit.jupiter.api.Test;
import model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions.*;


public class TrapTest {
    @Test
    void testTrapInitialization() {
        Position pos = new Position(1, 2);
        Trap trap = new Trap(pos, TrapState.PICKUP);

        assertEquals(pos, trap.getPosition());
        assertEquals(TrapState.PICKUP, trap.getState());
        assertTrue(trap.isActive());
    }

    @Test
    void testTrapStateChecks() {
        Trap pickupTrap = new Trap(new Position(0, 0), TrapState.PICKUP);
        Trap armedTrap = new Trap(new Position(1, 1), TrapState.ARMED);

        assertTrue(pickupTrap.isPickup());
        assertFalse(pickupTrap.isArmed());

        assertTrue(armedTrap.isArmed());
        assertFalse(armedTrap.isPickup());
    }
    @Test
    void testArmAtChangesStateAndPosition() {
        Trap trap = new Trap(new Position(0, 0), TrapState.PICKUP);
        Position newPos = new Position(2, 3);

        trap.armAt(newPos);

        assertEquals(newPos, trap.getPosition());
        assertEquals(TrapState.ARMED, trap.getState());
        assertTrue(trap.isActive());
    }
    @Test
    void testDeactivate() {
        Trap trap = new Trap(new Position(1, 1), TrapState.ARMED);

        trap.deactivate();

        assertFalse(trap.isActive());
        assertNull(trap.getPosition());
        assertFalse(trap.shouldIgnorePlayerUntilLeave());
    }
}
