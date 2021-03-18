package structures;

import java.util.concurrent.ConcurrentHashMap;

public class GameTree {
    private static final ConcurrentHashMap<Integer,
            ConcurrentHashMap<LocalState,GameTreeNode>> game_tree = new ConcurrentHashMap<>();

    public static void put(LocalState board, GameTreeNode node){
        var inner_map = game_tree.get(board.GetMoveNumber());
        if(inner_map == null) {
            inner_map = new ConcurrentHashMap<>();
            game_tree.put(board.GetMoveNumber()-1, inner_map);
            inner_map.put(board,node);
        } else if(!inner_map.containsKey(board)){
            inner_map.put(board,node);
        }
    }

    public static GameTreeNode get(LocalState board){
        var inner_map = game_tree.get(board.GetMoveNumber()-1);
        if(inner_map != null){
            return inner_map.get(board);
        }
        return null;
    }

    public static void remove(int move_num){
        var inner_map = game_tree.get(move_num);
        for(GameTreeNode node : inner_map.values()){
            node.disown_children();
        }
        game_tree.remove(move_num);
    }
}
