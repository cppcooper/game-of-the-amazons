package algorithms.search;

import algorithms.analysis.HeuristicsQueue;
import data.pod.Move;
import data.structures.GameState;
import data.structures.GameTree;
import data.structures.GameTreeNode;
import tools.Debug;
import tools.Maths;
import tools.RandomGen;
import tools.Tuner;
import main.AICore;

import java.util.*;

public class MonteCarlo {
    private static RandomGen rng = new RandomGen();

    public static boolean exploreDepthFirst(GameTreeNode root) {
        MonteCarlo mc = new MonteCarlo();
        mc.run_simulation(root, false, true);
        return !Thread.interrupted();
    }
    public static boolean exploreBreadthFirst(GameTreeNode root) {
        MonteCarlo mc = new MonteCarlo();
        mc.run_simulation(root, true, true);
        return !Thread.interrupted();
    }

    private Deque<GameTreeNode> simulation_queue = new LinkedList<>();
    private MonteCarlo(){}

    private void run_simulation(GameTreeNode parent, boolean breadth_first, boolean simulate) {
        assert simulation_queue != null;
        //System.out.printf("%d\n", board.GetMoveNumber());
        GameState board = parent.state_after_move.get();
        if (board.CanGameContinue() && !Thread.currentThread().isInterrupted()) {
            int round = AICore.GetCurrentMoveNumber();
            int branches = (int) Maths.lerp(Tuner.montecarlo_breadth_top, Tuner.montecarlo_breadth_bottom, Math.min(1, (board.GetMoveNumber() - round) / (92.0 - round)));
            TreeSet<GameTreeNode> candidates = new TreeSet<>(new GameTreeNode.NodeComparator());
            policy_type policy_type = rng.get_random_policy(board.GetMoveNumber());
            ArrayList<Move> moves = MoveCompiler.GetMoveList(board, board.GetTurnPieces(), true);
            if (moves == null || moves.isEmpty()) {
                Debug.RunVerboseL2DebugCode(() -> {
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
                    if (!Tuner.use_heuristic_queue && !node.heuristic.is_ready.get()) {
                        node.calculate_heuristics(false);
                    } else {
                        switch (policy_type) {
                            case REDUCTION:
                                node.heuristic.FillReduction(copy);
                                break;
                            case FREEDOM:
                                node.heuristic.FillFreedom(copy);
                                break;
                            case TERRITORY:
                                node.heuristic.FillTerritory(copy);
                                break;
                            case AMAZONGS:
                                node.heuristic.FillAmazongs(copy);
                                break;
                        }
                    }
                    candidates.add(node);
                }
            }
            //candidate list is built

            int b = 0;
            // add the best X positions to the simulation queue
            for (GameTreeNode node : candidates.descendingSet()) {
                if (b++ >= branches || Thread.currentThread().isInterrupted()) {
                    break;
                }
                if (breadth_first) {
                    // add to the back
                    simulation_queue.add(node);
                } else {
                    // add to the front
                    simulation_queue.push(node);
                }
            }
            if (simulate) {
                while (!simulation_queue.isEmpty()) {
                    // read from the front -> back
                    GameTreeNode node = simulation_queue.poll();
                    if (!Tuner.use_heuristic_queue) {
                        node.calculate_heuristics(false);
                    }
                    run_simulation(node, breadth_first, false);
                }
            }
        } else if (!board.CanGameContinue()) {
            parent.heuristic.FillWinner(parent.state_after_move.get());
            parent.propagate();
            Debug.RunVerboseL1DebugCode(() -> {
                System.out.printf("Terminal state found\npoints: %.3f\n", parent.heuristic.winner.get());
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
