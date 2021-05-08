package algorithms.search;

import data.parallel.GameTreeNode;
import tools.Benchmarker;
import tools.Debug;
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

    private void findBest(GameTreeNode root){
        if(!root.isTerminal() && root.isReady()) {
            double best = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < root.edges(); ++i) {
                if(B.elapsed() < Tuner.max_wait_time) {
                    GameTreeNode child = root.get(i);
                    if (child.isTerminal()) {
                        best_node = child;
                        return;
                    }
                    if (child.isReady()) {
                        double h = root.combined.get() - child.combined.get() + (child.maximum_sub.get() / 10.0);
                        if (h > best) {
                            best = h;
                            best_node = child;
                        }
                    }
                }
            }
        }
    }
}
