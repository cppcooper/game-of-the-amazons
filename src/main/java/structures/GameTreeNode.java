package structures;

import java.util.ArrayList;

public class GameTreeNode {
    public Move move = null;
    public ArrayList<GameTreeNode> next_nodes = new ArrayList<>();
    public GameTreeNode(Move move){
        this.move = move;
    }
}
