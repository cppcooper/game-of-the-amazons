package structures;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameTree {
    private static ConcurrentHashMap<Integer,
            ConcurrentHashMap<LocalState,GameTreeNode>> game_tree = new ConcurrentHashMap<>();
    private static int hash_prune_mask = (256-1) << 24;

    public static boolean put(LocalState board, GameTreeNode node){
        int hash_code = board.hashCode();
        int hash_8bit = hash_code & hash_prune_mask;
        var inner_map = game_tree.get(board.hashCode() & hash_prune_mask);
        if(inner_map == null) {
            inner_map = new ConcurrentHashMap<LocalState,GameTreeNode>();
            game_tree.put(hash_8bit, inner_map);
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
        var inner_map = game_tree.get(board.hashCode() & hash_prune_mask);
        if(inner_map != null){
            return inner_map.get(board);
        }
        return null;
    }
}
