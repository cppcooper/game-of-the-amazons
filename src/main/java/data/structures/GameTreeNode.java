package data.structures;

import data.parallel.Node;
import data.pod.Move;
import data.parallel.SynchronizedArrayList;
import org.apache.commons.math3.util.Precision;
import tools.Maths;
import tools.Tuner;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

/**
 * GameTreeNode represents a Move and the value that move has for the player making it
 * We track:
 * - past moves
 * - future moves
 * -
 */

public class GameTreeNode extends Node {
    public final Heuristic heuristic = new Heuristic();
    final public AtomicReference<Move> move = new AtomicReference<>();
    final public AtomicReference<GameState> state_after_move = new AtomicReference<>();

    public GameTreeNode(Move move, GameTreeNode parent, GameState state_after_move) {
        super(parent);
        this.move.set(move);
        this.state_after_move.set(state_after_move);
        if (parent != null) {
            parent.adopt(this);
        }
    }

    public GameTreeNode get(int index) {
        return (GameTreeNode) super.get(index);
    }

    @Override
    protected void add_parent(Node parent) {
        super.add_parent(parent);
        GameTreeNode p = (GameTreeNode) parent;
        // deal with merging tree branches
        if (heuristic.has_propagated.get()) { //if the node already told parents about children
            propagate_minmax();
            if (heuristic.has_aggregated.get()) { //if the node has any aggregation data
                double parents_new_aggregate = p.heuristic.aggregate.get() + heuristic.aggregate.get();
                int parents_new_aggregate_count = p.heuristic.aggregate_count.get() + heuristic.aggregate_count.get();
                p.propagate_aggregate(parents_new_aggregate, parents_new_aggregate_count);
            }
        }
    }

    public void calculate_heuristics(boolean skip_propagation) {
        GameState board = state_after_move.get();
        if(Tuner.use_winner_heuristic) {
            heuristic.FillWinner(board);
        }
        if (Tuner.use_mobility_heuristic) {
            heuristic.FillMobility(board);
        }
        if (Tuner.use_territory_heuristic) {
            heuristic.FillTerritory(board);
        }
        if (Tuner.use_amazongs_heuristic) {
            heuristic.FillAmazongs(board);
        }
        if (!heuristic.is_ready.get()) {
            heuristic.is_ready.set(true);
            double term1 = 0;
            double term2 = 0;
            double w = 1;
            if (Tuner.use_amazongs_heuristic) {
                term1 = heuristic.amazongs.get();
            }
            if (Tuner.use_territory_heuristic) {
                double t = heuristic.territory.get();
                term1 *= t;
                term2 += t;
            }
            if (Tuner.use_mobility_heuristic) {
                term2 += heuristic.mobility.get();
            }
            if (Tuner.use_winner_heuristic) {
                w = heuristic.winner.get();
            }
            double v = Maths.h(term1, term2, w);
            heuristic.value.set(v);
            if(!Tuner.use_winner_aggregate || Tuner.use_winner_heuristic || !board.CanGameContinue()) {
                if (!skip_propagation) {
                    propagate();
                } else {
                    set_aggregate();
                }
            }
        }
    }

    public synchronized void propagate() {
        if(!Tuner.disable_propagation_code) {
            if (!heuristic.has_propagated.get()) {
                propagate_minmax();
                if(!state_after_move.get().CanGameContinue()) {
                    double aggregate_value = Tuner.get_aggregate_base(heuristic);
                    if(Tuner.use_only_winning){
                        if(!Precision.equals(aggregate_value, 0, 0.0001)){
                            propagate_aggregate(aggregate_value, heuristic.aggregate_count.get() + 1);
                        }
                    } else {
                        propagate_aggregate(aggregate_value, heuristic.aggregate_count.get() + 1);
                    }
                }
            }
        }
    }
    private void propagate_minmax() {
        if(!Tuner.disable_propagation_code) {
            heuristic.has_propagated.set(true);
            double h = Tuner.get_aggregate_base(heuristic);
            for (int i = 0; i < super_nodes.size(); ++i) {
                GameTreeNode parent = get(i);
                if (parent.heuristic.maximum_sub.get() < h) {
                    parent.heuristic.has_max.set(true);
                    parent.heuristic.maximum_sub.set(h);
                }
                if (parent.heuristic.minimum_sub.get() > h) {
                    parent.heuristic.has_min.set(true);
                    parent.heuristic.minimum_sub.set(h);
                }
            }
        }
    }
    public void set_aggregate(){
        if(!Tuner.disable_propagation_code) {
            if (!heuristic.has_aggregated.get()) {
                heuristic.has_aggregated.set(true);
                double aggregate = heuristic.aggregate.add(Tuner.get_aggregate_base(heuristic));
                heuristic.aggregate_avg.set(aggregate / heuristic.aggregate_count.incrementAndGet());
            }
        }
    }
    private synchronized void propagate_aggregate(double new_aggregate, int new_aggregate_count) {
        if(!Tuner.disable_propagation_code) {
            heuristic.has_aggregated.set(true);
            new_aggregate = Math.max(new_aggregate, 0);
            int delta_count = new_aggregate_count - heuristic.aggregate_count.get();
            if (delta_count != 0) {
                double delta_aggregate = new_aggregate - heuristic.aggregate.get();
                double m = new_aggregate / new_aggregate_count;
                heuristic.aggregate.set(new_aggregate);
                heuristic.aggregate_avg.set(m);
                heuristic.aggregate_count.set(new_aggregate_count);
                if(Tuner.use_winner_aggregate && !Tuner.use_winner_heuristic){
                    heuristic.has_winner.set(true);
                    heuristic.winner.set(m);
                }
                for (int i = 0; i < super_nodes.size(); ++i) {
                    GameTreeNode parent = get(i);
                    double new_p_aggregate = parent.heuristic.aggregate.get() + delta_aggregate;
                    int new_p_aggregate_count = parent.heuristic.aggregate_count.get() + delta_count;
                    parent.propagate_aggregate(new_p_aggregate, new_p_aggregate_count);
                }
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
            if (h1.is_ready.get() && h2.is_ready.get()) {
                return Double.compare(h1.value.get(), h2.value.get());
            }
            if (h1.has_amazongs.get() && h2.has_amazongs.get()) {
                return Double.compare(h1.amazongs.get(), h2.amazongs.get());
            }
            if (h1.has_mobility.get() && h2.has_mobility.get()) {
                return Double.compare(h1.mobility.get(), h2.mobility.get());
            }
            if (h1.has_territory.get() && h2.has_territory.get()) {
                return Double.compare(h1.territory.get(), h2.territory.get());
            }
            if (h1.has_propagated.get() && h2.has_propagated.get()) {
                return Double.compare(h1.aggregate_avg.get(), h2.aggregate_avg.get());
            }
            return Double.compare(h1.mobility.get(), h2.mobility.get()); //this may be set, but not flagged (reduction/freedom)
        }
    }
}
