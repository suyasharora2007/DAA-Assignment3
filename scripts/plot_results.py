import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

CSV = "results/results.csv"
df = pd.read_csv(CSV)

# 1. Operations vs n
plt.figure(figsize=(10, 6))
for algorithm in df.algorithm.unique():
    for density, marker in [(4, "o"), (20, "s")]:
        subset = df[(df.algorithm == algorithm) & (df.m == df.n * density)]
        if subset.empty:
            continue
        # Use the algorithm's primary counted work. DSU operations are used for Kruskal;
        # edge scans for the traversal algorithms.
        y = subset.dsu_ops if algorithm == "Kruskal" else subset.edge_scans
        plt.loglog(subset.n, y, marker=marker, label=f"{algorithm} ({'sparse' if density == 4 else 'dense'})")
plt.xlabel("Number of vertices n")
plt.ylabel("Counted operations")
plt.title("Operations vs n")
plt.grid(True, which="both", alpha=0.25)
plt.legend(fontsize=8)
plt.tight_layout()
plt.savefig("results/plots/operations_vs_n.png", dpi=180)
plt.close()

# 2. Scans per edge
plt.figure(figsize=(10, 6))
for algorithm in df.algorithm.unique():
    for density, marker in [(4, "o"), (20, "s")]:
        subset = df[(df.algorithm == algorithm) & (df.m == df.n * density)]
        if subset.empty:
            continue
        ratio = subset.edge_scans / subset.m
        plt.semilogx(subset.n, ratio, marker=marker, label=f"{algorithm} ({'sparse' if density == 4 else 'dense'})")
plt.xlabel("Number of vertices n")
plt.ylabel("edgeScans / m")
plt.title("Scans per edge")
plt.grid(True, which="both", alpha=0.25)
plt.legend(fontsize=8)
plt.tight_layout()
plt.savefig("results/plots/scans_per_edge.png", dpi=180)
plt.close()

# 3. Kruskal share of edges examined
plt.figure(figsize=(9, 6))
for density, marker in [(4, "o"), (20, "s")]:
    subset = df[(df.algorithm == "Kruskal") & (df.m == df.n * density)]
    plt.semilogx(subset.n, subset.edge_scans / subset.m, marker=marker,
                 label="sparse m=4n" if density == 4 else "dense m=20n")
plt.xlabel("Number of vertices n")
plt.ylabel("Kruskal edgeScans / m")
plt.title("Share of edges Kruskal examines")
plt.grid(True, which="both", alpha=0.25)
plt.legend()
plt.tight_layout()
plt.savefig("results/plots/kruskal_edge_share.png", dpi=180)
plt.close()

# Slope values used in REPORT.md.
for algorithm in df.algorithm.unique():
    subset = df[df.algorithm == algorithm]
    if subset.empty:
        continue
    for density in (4, 20):
        s = subset[subset.m == subset.n * density]
        if len(s) >= 2:
            y = s.dsu_ops if algorithm == "Kruskal" else s.edge_scans
            slope = np.polyfit(np.log10(s.n), np.log10(y), 1)[0]
            print(f"{algorithm},{density}n,slope={slope:.4f}")
