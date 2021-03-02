package structures;

import java.util.concurrent.ConcurrentHashMap;

public class GameTree {
    private static ConcurrentHashMap<LocalState,GameTreeNode> game_tree = new ConcurrentHashMap<>();

    public static boolean put(LocalState board, GameTreeNode node){
        if(!game_tree.containsKey(board)){
            game_tree.put(board,node);
            return true;
        }
        return false;
    }

    public static GameTreeNode get(LocalState board){
        return game_tree.get(board);
    }
}
