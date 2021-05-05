package algorithms.exploration;

import data.structures.GameState;
import data.parallel.GameTreeNode;
import tools.Debug;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Explorer {
    private enum ExplorationMethod {
        exhaustive,
        montecarlo_bf,
        montecarlo_df,
        alpha_beta,
        montecarlo
    }
    private static final int num_functions = 5;
    private static final int num_threads = Math.min(num_functions, Runtime.getRuntime().availableProcessors() -1);
    private ExecutorService thread_pool = null;
    private GameState current_board = null;
    private GameTreeNode root = null;
    private CountDownLatch signal;

    protected static final AtomicBoolean game_tree_is_explored = new AtomicBoolean(false);
    protected final AtomicBoolean threads_terminating = new AtomicBoolean(false);


    public Explorer(GameTreeNode root, CountDownLatch signaler){
        this.root = root;
        signal = signaler;
        current_board = root.state_after_move.get();
    }

    public void start(){
        if(thread_pool == null) {
            thread_pool = Executors.newFixedThreadPool(num_threads);
            int i = 0;
            for (ExplorationMethod m : ExplorationMethod.values()) {
                if (i++ < num_threads) {
                    QueueExploration(m);
                }
            }
        }
    }

    public void stop(){
        try {
            if(thread_pool != null){
                threads_terminating.set(true);
                thread_pool.shutdownNow();
                thread_pool.awaitTermination(1, TimeUnit.SECONDS);
                thread_pool = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void QueueExploration(ExplorationMethod method){
        switch(method){
            case exhaustive:
                thread_pool.execute(this::Exhaustively);
                break;
            case alpha_beta:
                thread_pool.execute(this::AlphaBeta);
                break;
            case montecarlo:
                thread_pool.execute(this::MonteCarlo);
                break;
            case montecarlo_bf:
                thread_pool.execute(this::MonteCarlo_breadthFirst);
                break;
            case montecarlo_df:
                thread_pool.execute(this::MonteCarlo_depthFirst);
                break;
        }
    }

    private void Exhaustively() {
        if (!game_tree_is_explored.get()) {
            Debug.PrintThreadID("ExhaustiveSearch");
            Exhaustive explorer = new Exhaustive(signal);
            if (explorer.explore(root)) {
                System.out.println("\nGAME TREE IS NOW FULLY EXPLORED.\n");
                game_tree_is_explored.set(true);
            }
        }
    }
    private void AlphaBeta(){

    }
    private void MonteCarlo(){

    }
    private void MonteCarlo_breadthFirst(){
        Debug.PrintThreadID("MonteCarlo_breadthfirst");
        while (!game_tree_is_explored.get() && !threads_terminating.get()) {
            //MonteCarlo.exploreBreadthFirst(root);
        }
    }
    private void MonteCarlo_depthFirst(){
        Debug.PrintThreadID("MonteCarlo_depthFirst");
        while (!game_tree_is_explored.get() && !threads_terminating.get()) {
            //MonteCarlo.exploreDepthFirst(root);
        }
    }
}
