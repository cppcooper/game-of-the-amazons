package algorithms.analysis;

import org.apache.commons.math3.util.Pair;
import data.GameState;
import data.GameTree;
import data.GameTreeNode;
import data.Move;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class BreadFirstSearch {
    public static final AtomicBoolean first_depth_done = new AtomicBoolean(false);

    public static boolean Search(GameState board){
        first_depth_done.set(false);
        GameTreeNode sim_root = GameTree.get(board);
        if(sim_root == null){
            sim_root = new GameTreeNode(new Move(),null, board);
            GameTree.put(sim_root); //in the off chance our two threads run this line at the same time, the reference should be the same.. so it should not matter which gets there first
        }
        Search(board, sim_root, 0);
        return !Thread.interrupted();
    }

    public static void Search(GameState board, GameTreeNode parent, int depth){
        if(!board.IsGameOver() && !Thread.currentThread().isInterrupted()) {
            ArrayList<Move> moves = MoveCompiler.GetMoveList(board, board.GetTurnPieces(), true);
            if (moves == null || moves.size() == 0) {
                return;
            }
            Queue<Pair<GameState,GameTreeNode>> branch_jobs = new LinkedList<>();
            for(Move m : moves){
                if(Thread.currentThread().isInterrupted()){
                    return;
                }
                GameState new_state = new GameState(board);
                if(new_state.MakeMove(m, true, false)) {
                    GameTreeNode node = GameTree.get(new_state); // GameTreeNode might already exist for this state [original_state + move]
                    if (node == null) {
                        // LocalState is a new position
                        node = new GameTreeNode(m, parent, new_state);
                        parent.adopt(node);
                        Heuristics.enqueue(node);
                        GameTree.put(node);
                    } else { //no idea why parent == node
                        // This LocalState + Node have already been seen once.
                        // This might represent branches merging so..
                        // run the adoption procedure to ensure linkage and propagation of the heuristic (only one link, and only propagates if node's heuristic is non-zero)
                        parent.adopt(node);
                    }
                    if (parent != node) {
                        branch_jobs.add(new Pair<>(new_state, node));
                    }
                }
            }
            while(!branch_jobs.isEmpty()){
                var job = branch_jobs.poll();
                Search(job.getFirst(), job.getSecond(),1);
            }
        }
    }
}
