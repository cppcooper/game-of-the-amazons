package algorithms.exploration;


import data.structures.GameState;
import data.parallel.GameTreeNode;
import main.Game;
import org.junit.jupiter.api.Test;
import tools.Maths;
import tools.RandomGen;
import tools.Tuner;

import java.util.concurrent.CountDownLatch;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Deque;

public class MonteCarlo extends GameExplorer {
    private final RandomGen rng = new RandomGen();
    private final boolean breadth_first;
    private policy_type eval_type;
    private Deque<GameTreeNode> job_queue = new LinkedList<>();

    protected MonteCarlo(CountDownLatch signal, boolean breadth_first){
        super(signal);
        this.breadth_first = breadth_first;
    }

    public boolean explore(GameTreeNode root){
        explore(root, true);
        return !Thread.interrupted();
    }

    protected void explore(GameTreeNode root, boolean is_root_invocation){
        GameState board = root.state_after_move.get();
        eval_type = rng.get_random_policy(board.getRoundNum());
        Deque<GameTreeNode> children = cull(expandTree(root));
        if(breadth_first){
            job_queue.addAll(children);
        } else {
            children.addAll(job_queue);
            job_queue = children;
        }
        if(is_root_invocation){
            processJobs();
        }
    }

    @Override
    protected void evaluate(GameTreeNode node) {
        switch (eval_type) {
            case REDUCTION:
                node.evaluateReduction();
                break;
            case FREEDOM:
                node.evaluateFreedom();
                break;
            case TERRITORY:
                node.evaluateTerritory();
                break;
            case AMAZONGS:
                node.evaluateAmazongs();
                break;
        }
    }

    private LinkedList<GameTreeNode> cull(LinkedList<GameTreeNode> children){
        int game_round = Game.Get().getCurrentTurn();
        int board_round = children.get(0).state_after_move.get().getRoundNum();
        int branches = (int) Maths.lerp(Tuner.montecarlo_breadth_top, Tuner.montecarlo_breadth_bottom, Math.min(1, (board_round - game_round) / (92.0 - game_round)));
        LinkedList<GameTreeNode> culled = new LinkedList<>();
        Collections.sort(children);
        int b = 0;
        do {
            int N = children.size();
            int NU = Maths.tri_num(N);
            int selection = rng.nextInt(NU);
            int i;
            for(i = 0; i < N; ++i){
                int j = i + 1;
                int max_ubound = NU - Maths.tri_num(N - j);
                if(selection < max_ubound){
                    break;
                }
            }
            culled.add(children.get(i));
            children.remove(i);
        } while(++b < branches);
        return culled;
    }

    @Test
    void test_selections(){
        int N = 5;
        int NU = Maths.tri_num(N);
        System.out.printf("N: %d; tri_num(%d): %d\n", N, N, NU);
        int i;
        for(int selection = 0; selection < NU; ++selection) {
            //int selection = rng.nextInt(NU); //[    0    ][   1   ][  2  ][ 3 ][4]
            System.out.printf("  selection: %d\n", selection);
            for (i = 0; i < N; ++i) {
                int j = i + 1;
                int max_ubound = NU - Maths.tri_num(N - j);
                if (selection < max_ubound) {
                    System.out.printf("  i: %d\n", i);
                    break;
                }
            }
        }
    }

    public enum policy_type {
        FREEDOM,
        REDUCTION,
        TERRITORY,
        AMAZONGS
    }
}
