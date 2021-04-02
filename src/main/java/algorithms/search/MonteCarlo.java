package algorithms.search;

import algorithms.analysis.HeuristicsQueue;
import data.structures.GameState;
import data.structures.GameTree;
import data.structures.GameTreeNode;
import org.apache.commons.math3.util.Pair;
import data.*;
import tools.Benchmarker;
import tools.Debug;
import tools.RandomGen;

import java.util.*;

public class MonteCarlo {
    public static class SimPolicy{
        public int branches = 1;
        public int depth = 1;
        public SimPolicy(int branches, int depth){
            this.branches = branches;
            this.depth = depth;
        }
    }

    public static boolean RunSimulation(GameState board, GameTreeNode sim_root, SimPolicy sim_policy) {
        RandomGen rng = new RandomGen();
        Debug.RunInfoL3DebugCode(()-> {
            System.out.printf("Tree size: %d\n", GameTree.size());
            System.out.printf("Exploring %d branches of %d depths of the game tree.\n", sim_policy.branches, sim_policy.depth);
        });
        Benchmarker B = new Benchmarker();
        B.Start();
        RunSimulation(rng, board, sim_root, 1, sim_policy);
        //System.out.printf("Search took %d ms\n", B.Elapsed());
        return !Thread.interrupted(); //Assuming execution was interrupted then we need to clear that flag, and restart from the current LocalState
    }

    private static void RunSimulation(RandomGen rng, GameState board, GameTreeNode parent, int depth, SimPolicy policy) {
        if (depth < policy.depth && board.CanGameContinue() && !Thread.currentThread().isInterrupted()) {
            ArrayList<Move> moves = MoveCompiler.GetMoveList(board, board.GetTurnPieces(), true);
            if (moves == null || moves.size() == 0) {
                return;
            }
            int ideal_sample_size = moves.size() >> 1; // divide by two
            if(ideal_sample_size > 0){
                moves = PruneMoves(moves, rng, board, parent, new TreePolicy(ideal_sample_size, policy.branches));
            }
            if (moves == null || moves.size() == 0) {
                return;
            }
            List<Integer> rng_set = rng.GetDistinctSequenceShuffled(0, moves.size()-1, Math.min(policy.branches, moves.size()));
            Queue<Pair<GameState, GameTreeNode>> branch_jobs = new LinkedList<>();
            for (int b = 0; b < policy.branches && b < moves.size(); ++b) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                GameState new_state = new GameState(board);
                Move m = moves.get(rng_set.get(b));
                if(new_state.MakeMove(m, true, false)) {
                    GameTreeNode node = GameTree.get(new_state); // GameTreeNode might already exist for this state [original_state + move]
                    if (node == null) {
                        // LocalState is a new position
                        node = new GameTreeNode(m, parent, new_state);
                        parent.adopt(node);
                        HeuristicsQueue.add(node);
                        GameTree.put(node);
                    } else {
                        // This LocalState + Node have already been seen once.
                        // This might represent branches merging so..
                        // run the adoption procedure to ensure linkage and propagation of the heuristic (only one link, and only propagates if node's heuristic is non-zero)
                        // (no idea why parent might be equal to node)
                        parent.adopt(node);
                    }
                    if (parent != node) {
                        RunSimulation(rng, new_state, node, depth + 1, policy);
                    }
                }
            }
        }
    }

    public static class TreePolicy{
        public enum policy_type{
            MOBILITY,
            WINNER_LOSER,
            TERRITORY,
            AMAZONGS,
            ALL_HEURISTICS,
            DO_NOTHING
        }
        int sample_size;
        int max_return;
        TreePolicy(int N,int M){
            sample_size = N;
            max_return = M;
        }
    }

    private static ArrayList<Move> PruneMoves(ArrayList<Move> moves, RandomGen rng, GameState board, GameTreeNode parent, TreePolicy tree_policy){
        if(moves == null){
            return null;
        }
        tree_policy.sample_size = Math.min(tree_policy.sample_size, moves.size());
        tree_policy.max_return = Math.min(tree_policy.max_return, tree_policy.sample_size);
        if(tree_policy.max_return == moves.size() || tree_policy.sample_size == 0){
            return moves;
        }
        TreePolicy.policy_type policy_type = rng.get_random_policy(board.GetMoveNumber());
        TreeSet<GameTreeNode> sample = new TreeSet<>(new GameTreeNode.NodeComparator());
        List<Integer> selection = rng.GetDistinctSequenceShuffled(0, moves.size()-1, tree_policy.sample_size);
        for(int i = 0; i < tree_policy.sample_size; ++i){
            if(Thread.currentThread().isInterrupted()){
                return null;
            }
            GameState copy = new GameState(board);
            Move move = moves.get(selection.get(i));
            if(copy.MakeMove(move,true, false)) {
                GameTreeNode node = GameTree.get(copy);
                if (node == null) {
                    node = new GameTreeNode(move, parent, copy);
                    parent.adopt(node);
                    GameTree.put(node);
                }
                switch (policy_type) {
                    case WINNER_LOSER:
                        HeuristicsQueue.FillWinner(copy, node.heuristic);
                    case MOBILITY:
                        HeuristicsQueue.FillMobility(copy, node.heuristic);
                        HeuristicsQueue.add(node);
                        break;
                    case TERRITORY:
                        HeuristicsQueue.FillTerritory(copy, node.heuristic);
                        HeuristicsQueue.add(node);
                        break;
                    case AMAZONGS:
                        HeuristicsQueue.FillAmazongs(copy, node.heuristic);
                        HeuristicsQueue.add(node);
                        break;
                    case ALL_HEURISTICS:
                        // all of the above combined
                        HeuristicsQueue.CalculateHeuristicsAll(copy, node, false);
                        break;
                }
                sample.add(node);
            }
        }
        moves = new ArrayList<>(tree_policy.max_return);
        int i = 0;
        for(GameTreeNode n : sample.descendingSet()){
            if(Thread.currentThread().isInterrupted()){
                return null;
            }
            if(i++ < tree_policy.max_return) {
                moves.add(n.move.get());
            } else {
                return moves;
            }
        }
        return moves;
    }
}
