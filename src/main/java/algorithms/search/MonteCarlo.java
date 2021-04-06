package algorithms.search;

import algorithms.analysis.HeuristicsQueue;
import data.structures.GameState;
import data.structures.GameTree;
import data.structures.GameTreeNode;
import data.*;
import tools.Debug;
import tools.RandomGen;
import tools.Tuner;

import java.util.*;

public class MonteCarlo {

    public static boolean RunSimulation(GameState board, GameTreeNode sim_root, boolean breadthfirst, int branches) {
        RandomGen rng = new RandomGen();;
        if(breadthfirst) {
            RunSimulation(rng, board, sim_root, new LinkedList<>(), branches, true);
        } else {
            RunSimulation(rng, board, sim_root, null, branches, true);
        }
        return !Thread.interrupted(); //Assuming execution was interrupted then we need to clear that flag, and restart from the current LocalState
    }

    private static void RunSimulation(RandomGen rng, GameState board, GameTreeNode parent, Queue<GameTreeNode> farthest_nodes, int branches, boolean simulate) {
        if (board.CanGameContinue() && !Thread.currentThread().isInterrupted()) {
            TreeSet<GameTreeNode> candidates = new TreeSet<>(new GameTreeNode.NodeComparator());
            policy_type policy_type = rng.get_random_policy(board.GetMoveNumber());
            ArrayList<Move> moves = MoveCompiler.GetMoveList(board, board.GetTurnPieces(), true);
            if (moves == null || moves.isEmpty()) {
                Debug.RunVerboseL2DebugCode(()->{
                    System.out.printf("player %d no moves state:\n", board.GetPlayerTurn());
                    board.DebugPrint();
                });
                return;
            }
            for (int i = 0; i < moves.size(); ++i) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                GameState copy = new GameState(board);
                Move move = moves.get(i);
                if (copy.MakeMove(move, true, false)) {
                    GameTreeNode node = GameTree.get(copy);
                    if (node == null) {
                        node = new GameTreeNode(move, parent, copy);
                        parent.adopt(node);
                        GameTree.put(node);
                        HeuristicsQueue.add(node);
                    }
                    if (!Tuner.use_heuristic_queue && !node.heuristic.is_ready.get()){
                        HeuristicsQueue.CalculateHeuristicsAll(copy, node, false);
                    } else {
                        switch (policy_type) {
                            case REDUCTION:
                                HeuristicsQueue.FillReduction(copy, node.heuristic);
                                break;
                            case FREEDOM:
                                HeuristicsQueue.FillFreedom(copy, node.heuristic);
                                break;
                            case TERRITORY:
                                HeuristicsQueue.FillTerritory(copy, node.heuristic);
                                break;
                            case AMAZONGS:
                                HeuristicsQueue.FillAmazongs(copy, node.heuristic);
                                break;
                        }
                    }
                    candidates.add(node);
                }
            }

            int b = 0;
            for (GameTreeNode node : candidates.descendingSet()) {
                if(b++ >= branches || Thread.currentThread().isInterrupted()){
                    return;
                }
                if(farthest_nodes != null){
                    farthest_nodes.add(node);
                } else {
                    if(!Tuner.use_heuristic_queue){
                        HeuristicsQueue.CalculateHeuristicsAll(node.state_after_move.get(), node, false);
                    }
                    RunSimulation(rng, node.state_after_move.get(), node, farthest_nodes, branches, true);
                }
            }
            if(farthest_nodes != null && simulate) {
                while(!farthest_nodes.isEmpty()){
                    GameTreeNode node = farthest_nodes.poll();
                    if(!Tuner.use_heuristic_queue){
                        HeuristicsQueue.CalculateHeuristicsAll(node.state_after_move.get(), node, false);
                    }
                    RunSimulation(rng, node.state_after_move.get(), node, farthest_nodes, branches, false);
                }
            }
        } else if (!board.CanGameContinue()) {
            HeuristicsQueue.FillWinner(parent.state_after_move.get(), parent.heuristic);
            parent.propagate();
            Debug.RunVerboseL1DebugCode(()->{
                System.out.printf("Terminal state found\npoints: %.3f\n",parent.heuristic.winner.get());
            });
        }
    }

    public enum policy_type {
        FREEDOM,
        REDUCTION,
        TERRITORY,
        AMAZONGS
    }
}
