package structures;

import java.util.ArrayList;

public class GameTreeNode {
    public double heuristic = -1.0;
    public double aggregate = -1.0;
    public int aggregation_depth = 0;
    public Move move = null;
    public GameTreeNode super_node = null;
    protected ArrayList<GameTreeNode> sub_nodes = new ArrayList<>(); //note: that there is no way to remove nodes! this is by design!

    public GameTreeNode(Move move, GameTreeNode parent){
        this.super_node = parent;
        this.move = move;
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
