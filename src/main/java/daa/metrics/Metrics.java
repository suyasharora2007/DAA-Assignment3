
package daa.metrics;

public class Metrics {

    public long dsuOps;
    public long edgeScans;
    public long comparisons;
    public int maxDepth;
    public long timeMs;

    public void reset() {
        dsuOps = 0;
        edgeScans = 0;
        comparisons = 0;
        maxDepth = 0;
        timeMs = 0;
    }

    public void addDsuOps(long count) {
        dsuOps += count;
    }

    public void addEdgeScans(long count) {
        edgeScans += count;
    }

    public void addComparisons(long count) {
        comparisons += count;
    }

    public void updateMaxDepth(int depth) {
        maxDepth = Math.max(maxDepth, depth);
    }

    public void setTimeMs(long time) {
        timeMs = time;
    }
}
