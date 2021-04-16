package algorithms.search;

import data.pod.Move;
import data.structures.GameTreeNode;
import tools.Benchmarker;
import tools.Debug;
import tools.Tuner;

public class BestMove {
    private Benchmarker B = new Benchmarker();
    private GameTreeNode best_node = null;
    private Move best_move = null;
    private int depth_reached = 0;

    public static Move Get(GameTreeNode root){
        return new BestMove(root).best_move;
    }

    private BestMove(GameTreeNode root){
        best_node = find_best_node(root, true);
        if (best_node == null) {
            best_node = find_best_node(root, false);
        }
        if(best_node != null) {
            best_move = best_node.move.get();
        }
    }

    private GameTreeNode find_best_node(GameTreeNode root, boolean is_first_call){
        Best best = new Best();
        if (root == null) {
            Debug.NoParentNodeFound.set(true);
            System.out.println("BestMove.Get: GameTree can't find the state");
            throw new IllegalStateException("There absolutely should be a root node, and we can't find it.");
        } else if (root.edges() == 0) {
            Debug.ZeroEdgesDetected.set(true);
            System.out.println("BestMove.Get: Zero edges");
            throw new IllegalStateException(
                    String.format("This should mean we have lost the game, in which case this thread should have been terminated.\n" +
                            "[game can continue: %B]", root.state_after_move.get().CanGameContinue()));
        }
        Debug.RunInfoL2DebugCode(() -> System.out.printf("BestMove.Get: our root node has %d edges, now to find the best edge\n", root.edges()));
        for (int i = 0; i < root.edges(); ++i) {
            if(B.Elapsed() < Tuner.max_wait_time){
                GameTreeNode selection = root.get(i);
                if (!selection.heuristic.is_ready.get()) {
                    Debug.RunVerboseL1DebugCode(() -> System.out.printf("BestMove.Get: node not ready. [Node: %s]\n", selection));
                    selection.calculate_heuristics(true);
                }
                final int edge = i;
                Debug.RunVerboseL1DebugCode(() -> System.out.printf("BestMove.find_best_node: node %d\n%s", edge, selection));
                best.node = which_best(best, selection, is_first_call);
                continue;
            }
            break;
        }
        // We've found our best nodes, now we need to return
        if (best.node != null) {
            System.out.println("BestMove.find_best: found one");
            return best.node;
        }
        return null;
    }

    private GameTreeNode which_best(Best best, GameTreeNode selection, boolean use_recursion){
        double aggregate = Double.NEGATIVE_INFINITY;
        double heuristic;
        if(use_recursion) {
            depth_reached = 0; //get_heuristic() will update this variable
            heuristic = get_heuristic(selection, 1) / depth_reached;
        } else {
            heuristic = get_heuristic(selection, Tuner.max_search_depth - 1);
        }
        if (heuristic > 0) {
            if (heuristic >= best.value && aggregate >= best.agg) {
                best.value = heuristic;
                best.agg = aggregate;
                return selection;
            }
        }
        return best_node;
    }

    private double get_heuristic(GameTreeNode node, int depth){
        double heuristic = 0;
        if (depth < Tuner.max_search_depth) {
            // Recursively sum the best nodes' heuristics
            if (node != null && node.heuristic.is_ready.get() && node.heuristic.has_max.get()) {
                depth_reached = depth;
                heuristic = node.heuristic.value.get() - node.heuristic.maximum_sub.get();
                if(heuristic < 0){
                    // if we calculate a negative value, then this node is no good
                    if (depth == 1){
                        return Double.NEGATIVE_INFINITY;
                    }
                    depth_reached--;
                    return 0;
                }
                heuristic += get_heuristic(find_best_node(node, false), depth + 1);
            } else if (depth == 1) {
                // we don't have heuristic information available for this node
                return Double.NEGATIVE_INFINITY;
            }
        }
        return heuristic;
    }

    private class Best{
        private GameTreeNode node = null;
        private double value = Double.NEGATIVE_INFINITY;
        private double agg = Double.NEGATIVE_INFINITY;
    }
}
