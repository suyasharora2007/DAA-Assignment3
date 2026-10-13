
package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import daa.algorithms.DSU;

class DSUTest {

    @Test
    void mergesComponentsCorrectly() {
        DSU dsu = new DSU(5);

        assertTrue(dsu.union(0, 1));
        assertTrue(dsu.union(1, 2));
        assertFalse(dsu.union(0, 2));

        assertEquals(3, dsu.components());
        assertEquals(dsu.find(0), dsu.find(2));
        assertNotEquals(dsu.find(0), dsu.find(3));
    }

    @Test
    void countsOperations() {
        DSU dsu = new DSU(3);

        dsu.union(0, 1);

        assertEquals(2, dsu.getFindCalls());
        assertEquals(1, dsu.getSuccessfulUnions());
        assertEquals(3, dsu.getDsuOps());
    }

    @Test
    void rejectsInvalidElements() {
        DSU dsu = new DSU(3);

        assertThrows(
                IllegalArgumentException.class,
                () -> dsu.find(3)
        );
    }

    @Test
    void handlesEmptySet() {
        DSU dsu = new DSU(0);

        assertEquals(0, dsu.components());
        assertEquals(0, dsu.getDsuOps());
    }
}
