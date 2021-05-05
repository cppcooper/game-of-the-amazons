package algorithms.search;

import data.pod.Move;
import data.parallel.GameTreeNode;

public class BestMove {
    private BestMove(){}
    public static Move Get(GameTreeNode root){
        return BestNode.Get(root).move.get();
    }
}
