package structures;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class GameTreeNode {
    private final ArrayList<GameTreeNode> super_nodes = new ArrayList<>();
    private final ArrayList<GameTreeNode> sub_nodes = new ArrayList<>(); //note: that there is no way to remove nodes! this is by design!

    private int N = 0;
    private final AtomicDouble heuristic = new AtomicDouble();
    final public AtomicDouble aggregate_heuristic = new AtomicDouble();
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

    public synchronized int edges(){
        return sub_nodes.size();
    }

    public synchronized GameTreeNode get(int index){
        return sub_nodes.get(index);
    }

    private synchronized void add_parent(GameTreeNode parent){
        super_nodes.add(parent);
        double this_aggregate = aggregate_heuristic.get();
        if(this_aggregate > 0.f) {
            double parents_new_aggregate = parent.aggregate_heuristic.get() + this_aggregate;
            parent.propagate(parents_new_aggregate);
        }
    }

    public synchronized void adopt(GameTreeNode node){
        //we don't do anything with heuristics because they won't exist yet when this method is used (RunSim/PruneMoves)
        if (!sub_nodes.contains(node)) {
            node.add_parent(this);
            sub_nodes.add(node);
        }
    }

    public synchronized void disown_children(){
        for(GameTreeNode child : sub_nodes){
            child.super_nodes.clear(); // we're only going to be running this during memory cleanup
            // unless that changes this incorrect looking function is actually correct
        }
    }

    public synchronized double get_heuristic(){
        return heuristic.get();
    }

    public synchronized void add_heuristic(double new_value){
        //newMean = oldMean + (Data - oldMean) / N;
        double h = heuristic.get();
        double new_aggregate = aggregate_heuristic.get() - h;
        h = h + (new_value - h) / ++N;
        heuristic.set(h);
        new_aggregate += h;
        propagate(new_aggregate);
    }

    public synchronized void set_heuristic(double new_value, int N){
        this.N = N;
        double new_aggregate = aggregate_heuristic.get() - heuristic.get() + new_value;
        heuristic.set(new_value);
        propagate(new_aggregate);
    }

    public synchronized void propagate(double new_aggregate){
        // todo (1): consider changing the weighting of aggregation (currently 1:1 ratio; parent:child)
        // todo (debug): verify this function, and its uses ensure an unbroken chain of heuristic aggregating
        double delta_aggregate = new_aggregate - aggregate_heuristic.get();
        aggregate_heuristic.set(new_aggregate);
        for(GameTreeNode parent : super_nodes){
            double parents_new_aggregate = parent.aggregate_heuristic.get() + delta_aggregate;
            parent.propagate(parents_new_aggregate);
        }
    }

    public static class NodeComparator implements Comparator<GameTreeNode> {
        @Override
        public int compare(GameTreeNode o1, GameTreeNode o2) {
            return Double.compare(o1.aggregate_heuristic.get(), o2.aggregate_heuristic.get());
        }
    }
}
