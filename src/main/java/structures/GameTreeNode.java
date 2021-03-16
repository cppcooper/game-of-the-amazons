package structures;

import java.util.ArrayList;

public class GameTreeNode {
	public Move move = null;
	protected ArrayList<GameTreeNode> sub_nodes = new ArrayList<>(); //note: that there is no way to remove nodes! this is by design!
	public GameTreeNode parent = null;
	public double heurVal = 0;
	public double totalHeurVal = 0;

	public GameTreeNode(Move move){
		
		this.move = move;
	}

	public synchronized void adoptAll(ArrayList<GameTreeNode> nodes){

		for (GameTreeNode child : nodes) {
			child.setParent(this);
		}
		sub_nodes.addAll(nodes);
		/* The below code filters duplicates - it may require a hashCode function implemented for GameTreeNode
		 * Not sure what would cause duplicate..
		 * It would require a LocalState being revisited, but why that would happen is unclear (maybe a transposition I suppose?)
		 * I've commented it out, because:
		 *  Duplicates should presumably be the exact same object instance (identical memory addresses)
		 *  If duplicates do exist, which I believe should be rare in the first place..
		 *   I don't know that it actually will be a bad thing to keep them in and not filter them
		 * So.. I've disabled the line of code
		 * */
		//next_nodes = next_nodes.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
	}
	public synchronized void adopt(GameTreeNode node){
		//if(!next_nodes.contains(node))
		node.setParent(this);
		sub_nodes.add(node);
	}
	public synchronized GameTreeNode get(int index){
		return sub_nodes.get(index);
	}
	public synchronized int edges(){
		return sub_nodes.size();
	}
	public void setParent(GameTreeNode parent) {
		parent.adopt(this);
		this.parent = parent;
	}

	// The heurVal will have to be set from outside from calculated heuristics
	public void setHeurVal(double val) {
		this.heurVal = val;
	}

	public void setHeurVal(GameTreeNode node) {
		double aggregate = 0;
		if (sub_nodes.isEmpty()) {
			aggregate = heurVal;
		}else {
			for (GameTreeNode child: sub_nodes) {
				aggregate += child.getTotalHeurVal();
			}
		}
		node.totalHeurVal = aggregate;
	}

	public double getTotalHeurVal() {
		if (totalHeurVal == 0) {
			setHeurVal(this);
		}
		return this.totalHeurVal;
	}

	public double getHeurVal() {
		return this.heurVal;
	}
}
