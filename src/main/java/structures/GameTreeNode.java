package structures;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class GameTreeNode {
    public AtomicInteger heuristic = new AtomicInteger();
    public AtomicInteger aggregate = new AtomicInteger();
    public int aggregation_depth = 0;
    public Move move = null;
    public GameTreeNode super_node = null;
    protected ArrayList<GameTreeNode> sub_nodes = new ArrayList<>(); //note: that there is no way to remove nodes! this is by design!

    public GameTreeNode(Move move, GameTreeNode parent){
        this.super_node = parent;
        this.move = move;
        heuristic.set(Float.floatToIntBits(0.f));
        aggregate.set(Float.floatToIntBits(0.f));
        parent.adopt(this);
    }
    private synchronized void adopt(GameTreeNode node){
        sub_nodes.add(node);
    }
    public synchronized GameTreeNode get(int index){
        return sub_nodes.get(index);
    }
    public synchronized int edges(){
        return sub_nodes.size();
    }
}
