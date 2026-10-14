package daa;

import daa.algorithms.DSU;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

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
    void randomUnionsAgreeWithReference() {
        Random rnd = new Random(7);
        int n = 100;
        DSU dsu = new DSU(n);
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        for (int step = 0; step < 1000; step++) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            dsu.union(a, b);
            int ra = refFind(parent, a), rb = refFind(parent, b);
            if (ra != rb) parent[rb] = ra;

            int expected = 0;
            for (int i = 0; i < n; i++) if (refFind(parent, i) == i) expected++;
            assertEquals(expected, dsu.components());
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j += 17) {
                    assertEquals(refFind(parent, i) == refFind(parent, j),
                            dsu.find(i) == dsu.find(j));
                }
            }
        }
    }

    @Test
    void rejectsInvalidElements() {
        DSU dsu = new DSU(3);
        assertThrows(IllegalArgumentException.class, () -> dsu.find(3));
    }

    @Test
    void handlesEmptySet() {
        DSU dsu = new DSU(0);
        assertEquals(0, dsu.components());
        assertEquals(0, dsu.getDsuOps());
    }

    private static int refFind(int[] parent, int x) {
        while (parent[x] != x) x = parent[x];
        return x;
    }
}
