package algorithms.analysis;

import org.apache.commons.math3.util.Pair;
import structures.GameTree;
import structures.GameTreeNode;
import structures.LocalState;
import structures.Move;
import tools.RandomGen;

import java.util.ArrayList;
import java.util.List;

public class MonteCarlo {
    public static class SimPolicy{
        public enum policy_type{
            BREADTH_FIRST,
            MONTE_CARLO
        }
        public int branches = 1;
        public int depth = 1;
        public policy_type type;
        public SimPolicy(int branches, int depth, policy_type sim_type){
            this.branches = branches;
            this.depth = depth;
            this.type = sim_type;
        }
    }

    public static boolean RunSimulation(LocalState board, int player, SimPolicy sim_policy) {
        RandomGen rng = new RandomGen();
        GameTreeNode sim_root = GameTree.get(board);
        if(sim_root == null){
            // This should only be the case if this is the beginning of the game. todo: ensure this holds true?
            sim_root = new GameTreeNode(new Move(),null);
            GameTree.put(board,sim_root); //in the off chance our two threads run this line at the same time, the reference should be the same.. so it should not matter which gets there first
        }
        RunSimulation(rng, board, sim_root, player, sim_policy.branches, sim_policy.depth, sim_policy.type);
        return !Thread.interrupted(); //Assuming execution was interrupted then we need to clear that flag, and restart from the current LocalState
    }

    private static void RunSimulation(RandomGen rng, LocalState board, GameTreeNode parent, int player, int branches, int depth, SimPolicy.policy_type type) {
        /* Simulate X branches at Y depths
         * simulate X branches
         ** On each branch simulate X branches
         * repeat until at Y depth
         * */
        if (depth > 0 && !board.IsGameOver() && !Thread.currentThread().isInterrupted()) {
            player = player == 1 ? 2 : 1;
            ArrayList<Move> moves = MoveCompiler.GetMoveList(board, player == 1 ? board.GetP1Pieces() : board.GetP2Pieces(), true);
            switch (type) {
                case BREADTH_FIRST:
                    moves = PruneMoves(board, moves, new TreePolicy(0, 0, TreePolicy.policy_type.DO_NOTHING));
                    break;
                case MONTE_CARLO:
                    int sample_size = moves.size() >> 1;
                    moves = PruneMoves(board, moves, new TreePolicy(sample_size > 0 ? sample_size : moves.size(), branches, rng.get_random_policy()));
                    break;
            }
            if (moves == null || moves.size() == 0) {
                return;
            }
            List<Integer> rng_set = rng.GetSequenceShuffled(0, moves.size(), Math.min(branches, moves.size()));
            for (int b = 0; b < branches && b < moves.size(); ++b) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                /* Copy board
                 * Perform move on board's copy
                 * Check if GameTree has this board state already
                 * + retrieve existing node
                 * - make new node
                 * - update GameTree
                 * Perform simulation on this board state
                 * Update node according to simulations run under it
                 * */
                LocalState new_state = new LocalState(board);
                Move m = moves.get(rng_set.get(b));
                new_state.MakeMove(m, true);

                GameTreeNode node = GameTree.get(new_state); // GameTreeNode might already exist for this state [original_state + move]
                if (node == null) {
                    // LocalState is a new position
                    node = new GameTreeNode(m, parent);
                    parent.adopt(node);
                    Heuristics.enqueue(new Pair<>(new_state, node));
                    GameTree.put(new_state, node);
                } else {
                    // This LocalState + Node have already been seen once.
                    // This might represent branches merging so..
                    // run the adoption procedure to ensure linkage and propagation of the heuristic (only one link, and only propagates if node's heuristic is non-zero)
                    parent.adopt(node);
                }
                RunSimulation(rng, new_state, node, player, branches, depth - 1, type);
            }
        }
    }

    public static class TreePolicy{
        public enum policy_type{
            CHEAPEST,
            FIRST_DEGREE_MOVES,
            COUNT_HEURISTIC,
            TERRITORY,
            ALL_HEURISTICS,
            DO_NOTHING
        }
        policy_type type;
        int sample_size;
        int max_return;
        TreePolicy(int N,int M, policy_type T){
            sample_size = N;
            max_return = M;
            type = T;
        }
    }

    private static ArrayList<Move> PruneMoves(LocalState board, ArrayList<Move> moves, TreePolicy tree_policy){
        // todo (1): implement PruneMoves [needs tree policy and stuff]
        tree_policy.sample_size = Math.min(tree_policy.sample_size, moves.size());;
        tree_policy.max_return = Math.min(tree_policy.max_return, tree_policy.sample_size);
        if(tree_policy.sample_size == 0 || tree_policy.type == TreePolicy.policy_type.DO_NOTHING){
            return moves;
        }
        RandomGen rng = new RandomGen();
        ArrayList<Move> sample = new ArrayList<>();
        List<Integer> selection = rng.GetSequenceShuffled(0,moves.size(),tree_policy.sample_size);
        for(int i = 0; i < tree_policy.sample_size; ++i){
            LocalState copy = new LocalState(board);
            Move move = moves.get(selection.get(i));
            copy.MakeMove(move,false);
            double heuristic =  0.0;
            switch(tree_policy.type){
                case CHEAPEST:
                    // use data in the move list itself? count the moves with next in it?
                    // this gives us the number of arrow moves from that position.. which is sort of a dirty first degree moves count
                    break;
                case FIRST_DEGREE_MOVES:
                    // this would be a different value than CHEAPEST, but it should actually be equivalent (multiplied by a constant)
                    // so this is probably a redundant one to include
                    break;
                case COUNT_HEURISTIC:
                    var counts = Heuristics.GetCount(copy);
                    break;
                case TERRITORY:
                    // this is probably the most valuable (single) heuristic for pruning moves. It might also be the most expensive
                    break;
                case ALL_HEURISTICS:
                    // all of the above combined
                    break;
            }
        }
        return moves;
    }
}
