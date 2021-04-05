package algorithms.search;

import algorithms.analysis.HeuristicsQueue;
import data.structures.GameState;
import data.structures.GameTree;
import data.structures.GameTreeNode;
import data.*;
import tools.Debug;
import tools.RandomGen;

import java.util.*;

public class MonteCarlo {

    public static boolean RunSimulation(GameState board, GameTreeNode sim_root, int branches) {
        RandomGen rng = new RandomGen();
        RunSimulation(rng, board, sim_root, branches);
        return !Thread.interrupted(); //Assuming execution was interrupted then we need to clear that flag, and restart from the current LocalState
    }

    private static void RunSimulation(RandomGen rng, GameState board, GameTreeNode parent, int branches) {
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
                    candidates.add(node);
                }
            }

            int b = 0;
            for (GameTreeNode node : candidates.descendingSet()) {
                if(b++ >= branches || Thread.currentThread().isInterrupted()){
                    return;
                }
                RunSimulation(node.state_after_move.get(), node, branches);
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
