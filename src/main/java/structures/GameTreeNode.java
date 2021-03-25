package structures;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

public class GameTreeNode {
    private final SynchronizedArrayList<GameTreeNode> super_nodes = new SynchronizedArrayList<>();
    private final SynchronizedArrayList<GameTreeNode> sub_nodes = new SynchronizedArrayList<>(); //note: that there is no way to remove nodes! this is by design!
    public final Heuristic heuristic = new Heuristic();
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
        if(heuristic.has_propagated.get()){
            if(parent.heuristic.max_sub_heuristic.get() < heuristic.aggregate.get()){
                parent.heuristic.max_sub_heuristic.set(heuristic.aggregate.get());
            }
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
            heuristic.has_propagated.set(true);
            for (int i = 0; i < super_nodes.size(); ++i) {
                GameTreeNode parent = super_nodes.get(i);
                if(parent.heuristic.max_sub_heuristic.get() < heuristic.aggregate.get()){
                    parent.heuristic.max_sub_heuristic.set(heuristic.aggregate.get());
                }
            }
        }
    }

    public static class NodeComparator implements Comparator<GameTreeNode> {
        @Override
        public int compare(GameTreeNode o1, GameTreeNode o2) {
            return Double.compare(o1.heuristic.max_sub_heuristic.get(), o2.heuristic.max_sub_heuristic.get());
        }
    }
}
