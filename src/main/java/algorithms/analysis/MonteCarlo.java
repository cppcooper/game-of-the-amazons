package algorithms.analysis;

import structures.GameTree;
import structures.GameTreeNode;
import structures.LocalState;
import structures.Move;
import tools.RandomGen;

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
        RunSimulation(rng, board, player, sim_policy.branches, sim_policy.depth);
    }

    protected void RunSimulation(RandomGen rng, LocalState board, int player, int branches, int depth){
        /* Simulate X branches at Y depths
         * simulate X branches
         ** On each branch simulate X branches
         * repeat until at Y depth
         * */
        if(depth > 0) {
            player = player == 1 ? 2 : 1;
            List<Move> moves = MoveCompiler.GetMoveList(board, player == 1 ? board.GetP1Pieces() : board.GetP2Pieces());
            List<Integer> rng_set = rng.GetSequenceShuffled(0, moves.size(), branches);
            for (int b = 0; b < branches; ++b) {
                /* Copy board
                 * Perform move on board's copy
                 * Create new node for move
                 * */
                LocalState state = new LocalState(board);
                Move m = moves.get(rng_set.get(b));
                state.MakeMove(m, true);

                GameTreeNode node = GameTree.get(state);
                if (node == null) {
                    node = new GameTreeNode(m);
                    GameTree.put(state, node);
                }

                //todo: figure out how to link this GameTreeNode with the ones made inside this call (all the way up the stack and beyond)
                RunSimulation(rng, state, player, branches, depth - 1);
            }
        }
    }
}
