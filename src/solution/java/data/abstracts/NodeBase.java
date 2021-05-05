package data.abstracts;


import data.parallel.SynchronizedArrayList;

public abstract class NodeBase {
    protected final SynchronizedArrayList<NodeBase> super_nodes = new SynchronizedArrayList<>();
    protected final SynchronizedArrayList<NodeBase> sub_nodes = new SynchronizedArrayList<>(); //note: that there is no way to remove nodes! this is by design!

    protected NodeBase(NodeBase parent) {
        if (parent != null) {
            parent.adopt(this);
        }
    }

    public NodeBase get(int index) {
        return sub_nodes.get(index);
    }
    public int edges() {
        return sub_nodes.size();
    }

    public boolean adopt(NodeBase node) {
        //we don't do anything with heuristics because they won't exist yet when this method is used (RunSim/PruneMoves)
        if (this != node) { //no idea why node == this (other than it happens in the MonteCarlo else)
            if (!sub_nodes.contains(node)) {
                node.super_nodes.add(this);
                sub_nodes.add(node);
                return true;
            }
        }
        return false;
    }
    public void disown_children() {
        for (int i = 0; i < sub_nodes.size(); ++i) {
            sub_nodes.get(i).super_nodes.clear(); // we're only going to be running this during memory cleanup
            // unless that changes this incorrect looking function is actually correct
        }
    }
}
