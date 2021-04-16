package algorithms.search;

import data.pod.Move;
import data.structures.GameTreeNode;

public class BestMove {
    public static Move Get(GameTreeNode root){
        return BestNode.Get(root).move.get();
    }
}
