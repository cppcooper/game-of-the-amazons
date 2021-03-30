package data;

import tools.Debug;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

/** GameTreeNode represents a Move and the value that move has for the player making it
 * We track:
 *  - past moves
 *  - future moves
 *  -
 *
 * */

public class GameTreeNode {
    private final SynchronizedArrayList<GameTreeNode> super_nodes = new SynchronizedArrayList<>();
    private final SynchronizedArrayList<GameTreeNode> sub_nodes = new SynchronizedArrayList<>(); //note: that there is no way to remove nodes! this is by design!
    public final Heuristic heuristic = new Heuristic();
    final public AtomicReference<Move> move = new AtomicReference<>();
    final public AtomicReference<GameState> state_after_move = new AtomicReference<>();

    public GameTreeNode(Move move, GameTreeNode parent, GameState state_after_move){
        this.move.set(move);
        this.state_after_move.set(state_after_move);
        if(parent != null){
            parent.adopt(this);
        }
    }

    public GameTreeNode get(int index){
        return sub_nodes.get(index);
    }
    public int edges(){
        return sub_nodes.size();
    }

    private void add_parent(GameTreeNode parent){
        super_nodes.add(parent);
        // deal with merging tree branches [part 1]
        if(heuristic.has_propagated.get()){
            force_propagate();
        }
        // deal with merging tree branches [part 2]
        if(heuristic.has_aggregated.get()){
            double parents_new_aggregate = parent.heuristic.aggregate.get() + heuristic.aggregate.get();
            int parents_new_aggregate_count = parent.heuristic.aggregate_count.get() + heuristic.aggregate_count.get();
            parent.update_aggregate(parents_new_aggregate, parents_new_aggregate_count);
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

    public void propagate(){
        if(!heuristic.has_propagated.get()){
            force_propagate();
        }
        if(!heuristic.has_aggregated.get()){
            update_aggregate(heuristic.value.get(), 1);
        }
    }
    private void force_propagate(){
        heuristic.has_propagated.set(true);
        double h = heuristic.value.get();
        for (int i = 0; i < super_nodes.size(); ++i) {
            GameTreeNode parent = super_nodes.get(i);
            if(parent.heuristic.maximum_sub.get() < h){
                parent.heuristic.maximum_sub.set(h);
            }
            if(parent.heuristic.minimum_sub.get() > h){
                parent.heuristic.minimum_sub.set(h);
            }
        }
    }
    private void update_aggregate(double new_aggregate, int new_aggregate_count){
        heuristic.has_aggregated.set(true);
        int delta_count = new_aggregate_count - heuristic.aggregate_count.get();
        if(delta_count != 0) {
            double old_aggregate = heuristic.aggregate.get();
            double delta_aggregate = new_aggregate - old_aggregate;
            heuristic.aggregate.set(new_aggregate);
            heuristic.aggregate_avg.set(new_aggregate / new_aggregate_count);
            heuristic.aggregate_count.set(new_aggregate_count);
            for (int i = 0; i < super_nodes.size(); ++i) {
                GameTreeNode parent = super_nodes.get(i);
                double new_p_aggregate = parent.heuristic.aggregate.get() + delta_aggregate;;
                int new_p_aggregate_count = parent.heuristic.aggregate_count.get() + delta_count;
                parent.update_aggregate(new_p_aggregate, new_p_aggregate_count);
            }
//            Debug.RunLevel2DebugCode(()-> {
//                double v = heuristic.aggregate.get();
//                System.out.printf("aggregate total: %.3f\naggregate count: %d\naggregate: %.3f\n-----\n",new_aggregate, new_aggregate_count,v);
//            });
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameTreeNode that = (GameTreeNode) o;

        if (super_nodes != null ? !super_nodes.equals(that.super_nodes) : that.super_nodes != null) return false;
        if (sub_nodes != null ? !sub_nodes.equals(that.sub_nodes) : that.sub_nodes != null) return false;
        if (heuristic != null ? !heuristic.equals(that.heuristic) : that.heuristic != null) return false;
        if (move != null ? !move.equals(that.move) : that.move != null) return false;
        return state_after_move != null ? state_after_move.equals(that.state_after_move) : that.state_after_move == null;
    }

    @Override
    public int hashCode() {
        int result = super_nodes != null ? super_nodes.hashCode() : 0;
        result = 31 * result + (sub_nodes != null ? sub_nodes.hashCode() : 0);
        result = 31 * result + (heuristic != null ? heuristic.hashCode() : 0);
        result = 31 * result + (move != null ? move.hashCode() : 0);
        result = 31 * result + (state_after_move != null ? state_after_move.hashCode() : 0);
        return result;
    }

    // used in PruneMoves to sort moves according to best for us and least beneficial to the enemy
    public static class NodeComparator implements Comparator<GameTreeNode> {
        @Override
        public int compare(GameTreeNode o1, GameTreeNode o2) {
            int c1 = Double.compare(o1.heuristic.value.get(),o2.heuristic.value.get());
            if(c1 == 0){
                return Double.compare(o1.heuristic.maximum_sub.get(), o2.heuristic.maximum_sub.get());
            }
            return c1;
        }
    }
}
