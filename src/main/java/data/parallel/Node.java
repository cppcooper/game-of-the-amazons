package data.parallel;

import data.pod.Move;
import data.structures.GameState;
import data.structures.GameTreeNode;

public class Node {
    protected final SynchronizedArrayList<Node> super_nodes = new SynchronizedArrayList<>();
    protected final SynchronizedArrayList<Node> sub_nodes = new SynchronizedArrayList<>(); //note: that there is no way to remove nodes! this is by design!

    public Node(Node parent) {
        if (parent != null) {
            parent.adopt(this);
        }
    }

    public Node get(int index) {
        return sub_nodes.get(index);
    }
    public int edges() {
        return sub_nodes.size();
    }

    protected void add_parent(Node parent) {
        super_nodes.add(parent);
    }
    public void adopt(Node node) {
        //we don't do anything with heuristics because they won't exist yet when this method is used (RunSim/PruneMoves)
        if (this != node) { //no idea why node == this (other than it happens in the MonteCarlo else)
            if (!sub_nodes.contains(node)) {
                node.add_parent(this);
                sub_nodes.add(node);
            }
        }
    }
    public void disown_children() {
        for (int i = 0; i < sub_nodes.size(); ++i) {
            sub_nodes.get(i).super_nodes.clear(); // we're only going to be running this during memory cleanup
            // unless that changes this incorrect looking function is actually correct
        }
    }
}
