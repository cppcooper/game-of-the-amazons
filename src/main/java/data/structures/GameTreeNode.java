package data.structures;

import data.BoardPiece;
import data.Heuristic;
import data.Move;
import data.parallel.SynchronizedArrayList;
import tools.Maths;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

/**
 * GameTreeNode represents a Move and the value that move has for the player making it
 * We track:
 * - past moves
 * - future moves
 * -
 */

public class GameTreeNode {
    private final SynchronizedArrayList<GameTreeNode> super_nodes = new SynchronizedArrayList<>();
    private final SynchronizedArrayList<GameTreeNode> sub_nodes = new SynchronizedArrayList<>(); //note: that there is no way to remove nodes! this is by design!
    public final Heuristic heuristic = new Heuristic();
    final public AtomicReference<Move> move = new AtomicReference<>();
    final public AtomicReference<GameState> state_after_move = new AtomicReference<>();

    public GameTreeNode(Move move, GameTreeNode parent, GameState state_after_move) {
        this.move.set(move);
        this.state_after_move.set(state_after_move);
        if (parent != null) {
            parent.adopt(this);
        }
    }

    public GameTreeNode get(int index) {
        return sub_nodes.get(index);
    }

    public int edges() {
        return sub_nodes.size();
    }

    private void add_parent(GameTreeNode parent) {
        super_nodes.add(parent);
        // deal with merging tree branches
        if (heuristic.has_propagated.get()) { //if the node already told parents about children
            force_propagate();
            if (heuristic.has_aggregated.get()) { //if the node has any aggregation data
                double parents_new_aggregate = parent.heuristic.aggregate.get() + heuristic.aggregate.get();
                int parents_new_aggregate_count = parent.heuristic.aggregate_count.get() + heuristic.aggregate_count.get();
                parent.update_aggregate(parents_new_aggregate, parents_new_aggregate_count);
            }
        }
    }

    public void adopt(GameTreeNode node) {
        //we don't do anything with heuristics because they won't exist yet when this method is used (RunSim/PruneMoves)
        if (this != node) { //no idea why node == this (other than it happens in the MonteCarlo else)
            if (!sub_nodes.contains(node)) {
                node.add_parent(this);
                sub_nodes.add(node);
            }
        }
    }

    public void disown_children() {
        for (int i = 0; i < sub_nodes.size(); ++i) {
            sub_nodes.get(i).super_nodes.clear(); // we're only going to be running this during memory cleanup
            // unless that changes this incorrect looking function is actually correct
        }
    }

    public synchronized void propagate() {
        if (!heuristic.has_propagated.get()) {
            force_propagate();
            update_aggregate(heuristic.aggregate.get() + heuristic.value.get(), 1);
        }
    }

    public void one_node_aggregation(){
        if(!heuristic.is_ready.get()){
            heuristic.is_ready.set(true);
            double aggregate = heuristic.aggregate.add(heuristic.value.get());
            heuristic.aggregate_avg.set(aggregate / heuristic.aggregate_count.incrementAndGet());
        }
    }

    private void force_propagate() {
        heuristic.has_propagated.set(true);
        double h = heuristic.value.get();
        for (int i = 0; i < super_nodes.size(); ++i) {
            GameTreeNode parent = super_nodes.get(i);
            if (parent.heuristic.maximum_sub.get() < h) {
                parent.heuristic.maximum_sub.set(h);
            }
            if (parent.heuristic.minimum_sub.get() > h) {
                parent.heuristic.minimum_sub.set(h);
            }
        }
    }

    private synchronized void update_aggregate(double new_aggregate, int new_aggregate_count) {
        heuristic.has_aggregated.set(true);
        new_aggregate = Math.max(new_aggregate,0);
        int delta_count = new_aggregate_count - heuristic.aggregate_count.get();
        if (delta_count != 0) {
            double delta_aggregate = new_aggregate - heuristic.aggregate.get();
            heuristic.aggregate.set(new_aggregate);
            heuristic.aggregate_avg.set(new_aggregate / new_aggregate_count);
            heuristic.aggregate_count.set(new_aggregate_count);
            for (int i = 0; i < super_nodes.size(); ++i) {
                GameTreeNode parent = super_nodes.get(i);
                double new_p_aggregate = parent.heuristic.aggregate.get() + delta_aggregate;
                ;
                int new_p_aggregate_count = parent.heuristic.aggregate_count.get() + delta_count;
                parent.update_aggregate(new_p_aggregate, new_p_aggregate_count);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameTreeNode that = (GameTreeNode) o;
        if(move.get().equals(that.move.get())){
            return state_after_move.get().equals(that.state_after_move.get());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s%s",move,heuristic);
    }

    // used in PruneMoves to sort moves according to best for us and least beneficial to the enemy
    public static class NodeComparator implements Comparator<GameTreeNode> {
        @Override
        public int compare(GameTreeNode o1, GameTreeNode o2) {
            Heuristic h1 = o1.heuristic;
            Heuristic h2 = o2.heuristic;
            if (h1.has_aggregated.get() && h2.has_aggregated.get()) {
                return Double.compare(h1.aggregate_avg.get(), h2.aggregate_avg.get());
            }
            if (h1.is_ready.get()) {
                if (h2.is_ready.get()) {
                    return Double.compare(h1.value.get(), h2.value.get());
                } else if (h2.has_mobility.get()) {
                    return Double.compare(h1.mobility.get(), h2.mobility.get());
                } else if (h2.has_territory.get()) {
                    return Double.compare(h1.territory.get(), h2.territory.get());
                }
            }
            if (h2.is_ready.get()) {
                if (h1.has_mobility.get()) {
                    return Double.compare(h1.mobility.get(), h2.mobility.get());
                } else if (h1.has_territory.get()) {
                    return Double.compare(h1.territory.get(), h2.territory.get());
                }
            }
            if (h1.has_mobility.get() && h2.has_mobility.get()){
                return Double.compare(h1.mobility.get(), h2.mobility.get());
            }
            if (h1.has_territory.get() && h2.has_territory.get()){
                return Double.compare(h1.territory.get(), h2.territory.get());
            }
            return 0;
        }
    }
}
