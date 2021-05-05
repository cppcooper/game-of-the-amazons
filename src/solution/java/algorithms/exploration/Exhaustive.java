package algorithms.exploration;

import data.parallel.GameTreeNode;

import java.util.concurrent.CountDownLatch;

public class Exhaustive extends GameExplorer {
    public Exhaustive(CountDownLatch signal){
        super(signal);
    }

    public boolean explore(GameTreeNode root){
        evaluate(root);
        explore(root, true);
        return !Thread.interrupted();
    }

    protected void explore(GameTreeNode root, boolean is_root_invocation){
        job_queue.addAll(expandTree(root));
        if(is_root_invocation) {
            signal.countDown();
            processJobs();
        }
    }

    @Override
    protected void evaluate(GameTreeNode node) {
        node.evaluate();
        node.propagate();
    }
}
