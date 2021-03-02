package algorithms.analysis;

import structures.GameTreeNode;
import structures.LocalState;
import structures.Move;
import tools.RandomGen;

import java.util.ArrayList;
import java.util.List;

public class MonteCarlo {
    public class SimPolicy{
        public int branches = 1;
        public int depth = 1;
        SimPolicy(int branches, int depth){
            this.branches = branches;
            this.depth = depth;
        }
    }

    public void RunSimulation(LocalState board, int player, SimPolicy sim_policy) throws Exception {
        int depth = 0;
        RandomGen rng = new RandomGen();
        while(depth++ < sim_policy.depth) {
            ArrayList<Move> moves = GetMoveList(board, player);
            List<Integer> rng_set = rng.GetSequenceShuffled(0, moves.size(), sim_policy.branches);

            for(int b = 0; b < sim_policy.branches; ++b){
                LocalState s = new LocalState(board);
                Move m = moves.get(rng_set.get(b));
                s.MakeMoves(m,true);

                GameTreeNode current_node = new GameTreeNode();
                current_node.move = m;

            }
        }
    }

    protected ArrayList<Move> GetMoveList(LocalState board, int player) throws Exception {
        if(player == 0 || player > 2){
            throw new Exception("Invalid player");
        }
        if(player == 1){
            return MoveCompiler.GetMoveList(board,board.GetP1Pieces());
        } else {
            return MoveCompiler.GetMoveList(board,board.GetP2Pieces());
        }
    }
}
