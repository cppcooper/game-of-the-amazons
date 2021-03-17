package algorithms.analysis;

import structures.GameTree;
import structures.GameTreeNode;
import structures.LocalState;
import structures.Move;
import tools.RandomGen;

import java.util.ArrayList;
import java.util.List;

public class MonteCarlo {
    public static class SimPolicy{
        public int branches = 1;
        public int depth = 1;
        public SimPolicy(int branches, int depth){
            this.branches = branches;
            this.depth = depth;
        }
    }

    public static boolean RunSimulation(LocalState board, int player, SimPolicy sim_policy) {
        RandomGen rng = new RandomGen();
        GameTreeNode sim_root = GameTree.get(board);
        if(sim_root == null){
            sim_root = new GameTreeNode(new Move(),null);
            GameTree.put(board,sim_root); //in the off chance our two threads run this line at the same time, the reference should be the same.. so it should not matter which gets there first
        }
        RunSimulation(rng, board, sim_root, player, sim_policy.branches, sim_policy.depth);
        return !Thread.interrupted(); //Assuming execution was interrupted then we need to clear that flag, and restart from the current LocalState
    }

    protected static void RunSimulation(RandomGen rng, LocalState board, GameTreeNode parent, int player, int branches, int depth){
        /* Simulate X branches at Y depths
         * simulate X branches
         ** On each branch simulate X branches
         * repeat until at Y depth
         * */
        if(depth > 0 && !board.IsGameOver() && !Thread.currentThread().isInterrupted()) {
            player = player == 1 ? 2 : 1;
            ArrayList<Move> moves = MoveCompiler.GetMoveList(board, player == 1 ? board.GetP1Pieces() : board.GetP2Pieces(), true);
            moves = PruneMoves(moves,new TreePolicy(0,0));
            if(moves == null){
                return;
            }
            List<Integer> rng_set = rng.GetSequenceShuffled(0, moves.size(), Math.min(branches, moves.size()));
            for (int b = 0; b < branches && b < moves.size(); ++b) {
                if(Thread.currentThread().isInterrupted()){
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
                    node = new GameTreeNode(m, parent);
                    GameTree.put(new_state, node);
                    // todo (1,dan): add something to concurrent queue for heuristics processing. Probably need both the state and node..
                } else {
                    // node already exists, we should assume that its branch is unreachable. ie. we should update its parent
                    node.super_node = parent;
                    // todo (1,dan): (conditionally) add something to concurrent queue for heuristics processing. Probably need both the state and node..
                }
                RunSimulation(rng, new_state, node, player, branches, depth - 1);
            }
        }
    }

    protected static class TreePolicy{
        TreePolicy(float a,int b){}
    }

    protected static ArrayList<Move> PruneMoves(ArrayList<Move> moves, TreePolicy tree_policy){
        // todo (2): implement tree policy and stuff (ie. this function [PruneMoves])

        /* This function should prune the move list such that we're left with X number of moves
        * X/2 should be moves that consider the most promising branches to explore
        * X/2 should also be moves that consider areas that haven't been explored well
        *
        * To implement such functionality (probably not even in the form this function intends) the algorithm above will probably need to be redesigned
        * Additionally the GameTree will likely need refactoring
        * Perhaps not though.
        * */
        return moves;
    }
}
