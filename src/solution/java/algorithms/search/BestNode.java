package algorithms.search;

import data.parallel.GameTreeNode;
import tools.Benchmarker;
import tools.Tuner;

public class BestNode {
    protected Benchmarker B = new Benchmarker();
    protected GameTreeNode best_node = null;

    public static GameTreeNode Get(GameTreeNode root) {
        return new BestNode(root).best_node;
    }

    protected BestNode(GameTreeNode root){
        B.start();
        findBest(root);
    }

    private void findBest(GameTreeNode root) {
        double best = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < root.edges(); ++i) {
            if (B.elapsed() < Tuner.max_wait_time) {
                GameTreeNode child = root.get(i);
                if (child.isTerminal()) {
                    best_node = child;
                    return;
                }
                if (child.isReady()) {
                    double h = child.combined.get() - child.maximum_sub.get();
                    if (h > best) {
                        best = h;
                        best_node = child;
                    }
                }
                continue;
            }
            return;
        }
    }
}
