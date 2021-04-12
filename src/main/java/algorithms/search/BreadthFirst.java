package algorithms.search;

import algorithms.analysis.HeuristicsQueue;
import data.structures.GameTreeNode;
import data.structures.GameState;
import data.structures.GameTree;
import data.Move;
import tools.Debug;
import tools.Tuner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class BreadthFirst {
    private static Random rng = new Random();

    public static boolean ExploreGameTree(GameState board){
        GameTreeNode sim_root = GameTree.get(board);
        if(sim_root == null){
            sim_root = new GameTreeNode(new Move(),null, board);
            GameTree.put(sim_root); //in the off chance our two threads run this line at the same time, the reference should be the same.. so it should not matter which gets there first
        }
        ExploreGameTree(board, sim_root, 0);
        return !Thread.interrupted();
    }

    private static void ExploreGameTree(GameState board, GameTreeNode parent, int depth){
        if(board.CanGameContinue() && !Thread.currentThread().isInterrupted()) {
            ArrayList<Move> moves = MoveCompiler.GetMoveList(board, board.GetTurnPieces(), true);
            if (moves == null || moves.isEmpty()) {
                return;
            }
            Queue<GameTreeNode> branch_jobs = new LinkedList<>();
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
                        if(Tuner.use_heuristic_queue && depth > 1) {
                            HeuristicsQueue.add(node);
                        } else {
                            HeuristicsQueue.CalculateHeuristicsAll(new_state, node, true);
                        }
                        GameTree.put(node);
                    } else { //no idea why parent == node
                        // This LocalState + Node have already been seen once.
                        // This might represent branches merging so..
                        // run the adoption procedure to ensure linkage and propagation of the heuristic (only one link, and only propagates if node's heuristic is non-zero)
                        parent.adopt(node);
                    }
                    if (parent != node) {
                        branch_jobs.add(node);
                    }
                }
            }
            while(!branch_jobs.isEmpty()){
                var job = branch_jobs.poll();
                ExploreGameTree(job.state_after_move.get(), job,depth+1);
            }
        } else if (!board.CanGameContinue()) {
            HeuristicsQueue.FillWinner(parent.state_after_move.get(), parent.heuristic);
            parent.propagate();
            Debug.RunVerboseL1DebugCode(()->{
                System.out.printf("Terminal state found\npoints: %.3f\n",parent.heuristic.winner.get());
            });
        }
    }
}
