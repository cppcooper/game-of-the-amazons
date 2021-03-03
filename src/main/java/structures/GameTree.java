package structures;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameTree {
    private static final ConcurrentHashMap<Integer,
            ConcurrentHashMap<LocalState,GameTreeNode>> game_tree = new ConcurrentHashMap<>();

    public static boolean put(LocalState board, GameTreeNode node){
        var inner_map = game_tree.get(board.GetMoveNumber());
        if(inner_map == null) {
            inner_map = new ConcurrentHashMap<LocalState,GameTreeNode>();
            game_tree.put(board.GetMoveNumber(), inner_map);
            inner_map.put(board,node);
            return true;
        }
        if(!inner_map.containsKey(board)){
            inner_map.put(board,node);
            return true;
        }
        return false;
    }

    public static GameTreeNode get(LocalState board){
        var inner_map = game_tree.get(board.GetMoveNumber());
        if(inner_map != null){
            return inner_map.get(board);
        }
        return null;
    }
}
