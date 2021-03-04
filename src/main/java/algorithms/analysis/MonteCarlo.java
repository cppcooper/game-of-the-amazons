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
        RunSimulation(rng, board, player, sim_policy.branches, sim_policy.depth);
        if(sim_root == null){
            //maybe we just don't worry about it?
        }
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
                }
                simulated_nodes.add(node);

                //todo: figure out how to link this GameTreeNode with the ones made inside this call (all the way up the stack and beyond)
                var children = RunSimulation(rng, state, player, branches, depth - 1);
                node.adoptAll(children);
            }
            return simulated_nodes;
        }
        return null;
    }
}
