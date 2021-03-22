package structures;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GameTreeNode {
    private final SynchronizedArrayList<GameTreeNode> super_nodes = new SynchronizedArrayList<>();
    private final SynchronizedArrayList<GameTreeNode> sub_nodes = new SynchronizedArrayList<>(); //note: that there is no way to remove nodes! this is by design!

    private final AtomicInteger N = new AtomicInteger(0);
    private final AtomicDouble heuristic_sum = new AtomicDouble();
    final public AtomicDouble aggregate_heuristic = new AtomicDouble();
    final public AtomicInteger aggregate_count = new AtomicInteger(0);
    final public AtomicBoolean has_first_degree = new AtomicBoolean(false);
    final public AtomicBoolean has_count = new AtomicBoolean(false);
    final public AtomicBoolean has_territory = new AtomicBoolean(false);
    final public AtomicReference<Move> move = new AtomicReference<>(null);

    public GameTreeNode(Move move, GameTreeNode parent){
        this.move.set(move);
        if(parent != null){
            parent.adopt(this);
        }
    }

    public int edges(){
        return sub_nodes.size();
    }

    public GameTreeNode get(int index){
        return sub_nodes.get(index);
    }

    private void add_parent(GameTreeNode parent){
        super_nodes.add(parent);
        double this_aggregate = aggregate_heuristic.get();
        if(this_aggregate > 0.f) {
            double parents_new_aggregate = parent.aggregate_heuristic.get() + this_aggregate;
            int parents_new_aggregate_count = parent.aggregate_count.get() + aggregate_count.get();
            parent.update_aggregate(parents_new_aggregate,parents_new_aggregate_count);
        }
    }

    public void adopt(GameTreeNode node){
        //we don't do anything with heuristics because they won't exist yet when this method is used (RunSim/PruneMoves)
        if(this != node) { //no idea why node == this (other than it happens in the MonteCarlo else)
            if (!sub_nodes.contains(node)) {
                node.add_parent(this);
                sub_nodes.add(node);
            }
        }
    }

    public void disown_children(){
        for(int i = 0; i < sub_nodes.size(); ++i){
            sub_nodes.get(i).super_nodes.clear(); // we're only going to be running this during memory cleanup
            // unless that changes this incorrect looking function is actually correct
        }
    }

    public double get_heuristic(){
        return heuristic_sum.get();
    }

    public int get_heuristic_count(){
        return N.get();
    }

    public void add_heuristic(double new_value){
        //newMean = oldMean + (Data - oldMean) / N;
        assert new_value >= 0;
        int current_aggregate_count = aggregate_count.get();
        int new_aggregate_count = current_aggregate_count + 1;
        // avg * N = total; (total+X) = new_total
        double new_aggregate_total = (aggregate_heuristic.get() * current_aggregate_count) + new_value;
        // make updates
        heuristic_sum.set(heuristic_sum.get() + new_value);
        update_aggregate(new_aggregate_total, new_aggregate_count);
    }

    public void set_heuristic(double new_value, int new_N){
        assert new_value >= 0;
        int old_N = this.N.get();
        int current_aggregate_count = aggregate_count.get();
        int new_aggregate_count = current_aggregate_count - old_N + new_N;
        // avg * N = total; (total - sub_total + new_sub_total) = new_total
        double new_aggregate_total = (aggregate_heuristic.get() * current_aggregate_count) - heuristic_sum.get() + new_value;
        // make updates
        this.N.set(new_N);
        heuristic_sum.set(new_value);
        update_aggregate(new_aggregate_total,new_aggregate_count);
    }

    public void update_aggregate(double new_aggregate_total, int new_aggregate_count){
        // todo (1): consider changing the weighting of aggregation (currently 1:1 ratio; parent:child)
        // todo (debug): verify this function, and its uses ensure an unbroken chain of heuristic aggregating
        // current_avg * current_N = current_total
        double old_aggregate_total = aggregate_heuristic.get() * aggregate_count.get();
        int delta_count = new_aggregate_count - aggregate_count.get();
        double delta_aggregate_total = new_aggregate_total - old_aggregate_total;
        // "current" (^^^) is now old
        if(new_aggregate_count != 0) {
            aggregate_heuristic.set(new_aggregate_total / new_aggregate_count);
            aggregate_count.set(new_aggregate_count);
            for (int i = 0; i < super_nodes.size(); ++i) {
                GameTreeNode parent = super_nodes.get(i);
                int parents_current_aggregate_count = parent.aggregate_count.get();
                int parents_new_aggregate_count = parents_current_aggregate_count + delta_count;

                // p.avg * p.N = p.Total; p.Total + delta_aggregate_total = p.newTotal
                double parents_new_aggregate_total = (parent.aggregate_heuristic.get() * parents_current_aggregate_count) + delta_aggregate_total;
                parent.update_aggregate(parents_new_aggregate_total, parents_new_aggregate_count);
            }
        }
    }

    public static class NodeComparator implements Comparator<GameTreeNode> {
        @Override
        public int compare(GameTreeNode o1, GameTreeNode o2) {
            return Double.compare(o1.aggregate_heuristic.get(), o2.aggregate_heuristic.get());
        }
    }
}
