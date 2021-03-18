package structures;

import java.util.ArrayList;

public class GameTreeNode {
    protected ArrayList<GameTreeNode> super_nodes = new ArrayList<>();
    protected ArrayList<GameTreeNode> sub_nodes = new ArrayList<>(); //note: that there is no way to remove nodes! this is by design!
    public AtomicFloat aggregate_heuristic = new AtomicFloat();
    public Move move;

    public GameTreeNode(Move move, GameTreeNode parent){
        this.move = move;
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
        float this_aggregate = aggregate_heuristic.get();
        if(this_aggregate > 0.f) {
            float parents_new_aggregate = parent.aggregate_heuristic.get() + this_aggregate;
            parent.propagate(parents_new_aggregate);
        }
    }

    public synchronized void adopt(GameTreeNode node){
        if(!sub_nodes.contains(node)) {
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

    public synchronized void propagate(float new_aggregate){
        // todo (1): consider changing the weighting of aggregation (currently 1:1 ratio; parent:child)
        // todo (1): verify this function, and its uses ensure an unbroken chain of heuristic aggregating
        float delta_aggregate = new_aggregate - aggregate_heuristic.get();
        aggregate_heuristic.set(new_aggregate);
        for(GameTreeNode parent : super_nodes){
            float parents_new_aggregate = parent.aggregate_heuristic.get() + delta_aggregate;
            parent.propagate(parents_new_aggregate);
        }
    }
}
