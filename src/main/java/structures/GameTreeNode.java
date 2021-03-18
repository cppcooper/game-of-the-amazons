package structures;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GameTreeNode {
    public AtomicInteger heuristic = new AtomicInteger();
    public AtomicInteger aggregate = new AtomicInteger();
    public int aggregation_depth = 0;
    public Move move = null;
    public AtomicReference<GameTreeNode> super_node = new AtomicReference<>();
    protected ArrayList<GameTreeNode> sub_nodes = new ArrayList<>(); //note: that there is no way to remove nodes! this is by design!

    public GameTreeNode(Move move){
        this.move = move;
        heuristic.set(Float.floatToIntBits(0.f));
        aggregate.set(Float.floatToIntBits(0.f));
    }
    public synchronized void adopt(GameTreeNode node, boolean aggregate){
        if(aggregate && !sub_nodes.contains(node)) {
            float this_agg = Float.intBitsToFloat(this.aggregate.get());
            float child_agg = Float.intBitsToFloat(node.aggregate.get());
            this_agg += child_agg;
            this.aggregate.set(Float.floatToIntBits(this_agg));
        }
        node.super_node.set(this);
        sub_nodes.add(node);
    }
    public synchronized void disown_children(){
        for(GameTreeNode child : sub_nodes){
            child.super_node.set(null);
        }
    }
    public synchronized GameTreeNode get(int index){
        return sub_nodes.get(index);
    }
    public synchronized int edges(){
        return sub_nodes.size();
    }
    public synchronized void propogate(){
        GameTreeNode parent = super_node.get();
        float parent_agg = Float.intBitsToFloat(parent.aggregate.get());
        float child_agg = Float.intBitsToFloat(aggregate.get());
        float prev_child_agg = 0;
        // todo (1): verify aggregation update logic
        while(parent != null){
            float temp = parent_agg;
            // todo (2): consider changing the weighting of aggregation (currently 1:1 ratio; parent:child)
            //update parent's aggregation value, because the child's changed
            parent_agg -= prev_child_agg;
            parent_agg += child_agg;
            parent.aggregate.set(Float.floatToIntBits(parent_agg));

            //update variables for next iteration
            parent = parent.super_node.get();
            parent_agg = Float.intBitsToFloat(parent.heuristic.get());
            child_agg = parent_agg;
            prev_child_agg = temp;
        }
    }
}
