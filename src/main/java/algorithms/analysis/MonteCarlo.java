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
        SimPolicy(int branches, int depth){
            this.branches = branches;
            this.depth = depth;
        }
    }

    public void RunSimulation(LocalState board, int player, SimPolicy sim_policy) {
        RandomGen rng = new RandomGen();
        GameTreeNode sim_root = GameTree.get(board);
        var children = RunSimulation(rng, board, player, sim_policy.branches, sim_policy.depth);
        if(sim_root != null){
            sim_root.adoptAll(children);
        }
        //todo: determine if we should worry about null root nodes? (it probably just means our opponent made a move, and we had not simulated it)
    }

    protected ArrayList<GameTreeNode> RunSimulation(RandomGen rng, LocalState board, int player, int branches, int depth){
        /* Simulate X branches at Y depths
         * simulate X branches
         ** On each branch simulate X branches
         * repeat until at Y depth
         * */
        ArrayList<GameTreeNode> simulated_nodes = new ArrayList<>(branches);
        if(depth > 0) {
            player = player == 1 ? 2 : 1;
            List<Move> moves = MoveCompiler.GetMoveList(board, player == 1 ? board.GetP1Pieces() : board.GetP2Pieces());
            List<Integer> rng_set = rng.GetSequenceShuffled(0, moves.size(), branches);
            for (int b = 0; b < branches; ++b) {
                /* Copy board
                 * Perform move on board's copy
                 * Check if GameTree has this board state already
                 * + retrieve existing node
                 * - make new node
                 * - update GameTree
                 * Perform simulation on this board state
                 * Update node according to simulations run under it
                 * */
                LocalState state = new LocalState(board);
                Move m = moves.get(rng_set.get(b));
                state.MakeMove(m, true);

                GameTreeNode node = GameTree.get(state);
                if (node == null) {
                    node = new GameTreeNode(m);
                    GameTree.put(state, node);
                    //todo: add something to concurrent queue for heuristics processing. Probably need both the state and node..
                }
                simulated_nodes.add(node);

                var children = RunSimulation(rng, state, player, branches, depth - 1);
                node.adoptAll(children);
            }
            return simulated_nodes;
        }
        return null;
    }

    protected static class TreePolicy{

    }

    protected ArrayList<Move> PruneMoves(ArrayList<Move> moves, TreePolicy tree_policy){
        //todo: implement tree policy stuff

        /* This function should prune the move list such that we're left with X number of moves
        * X/2 should be moves that consider the most promising branches to explore
        * X/2 should also be moves that consider areas that haven't been explored well
        *
        * To implement such functionality (probably not even in the form this function intends) the algorithm above will probably need to be redesigned
        * Additionally the GameTree will likely need refactoring
        * Perhaps not though.
        * */
        return null;
    }
}
