package structures;

import java.util.concurrent.ConcurrentHashMap;

public class GameTree {
    private static final ConcurrentHashMap<Integer,
            ConcurrentHashMap<LocalState,GameTreeNode>> game_tree = new ConcurrentHashMap<>();

    //todo: once we have a way to rank states, we're gonna want to implement and algorithm to FindBestBranch for any given level (which will likely require a depth first search to find the best terminal state)
    //this algorithm might necessitate increasing the time complexity of pruning the game tree such that unlinking nodes is necessary (ie. Node become backwards linked and thereby need to be disconnected when we drop them from the table)

    public static void put(LocalState board, GameTreeNode node){
        var inner_map = game_tree.get(board.GetMoveNumber());
        if(inner_map == null) {
            inner_map = new ConcurrentHashMap<>();
            game_tree.put(board.GetMoveNumber(), inner_map);
            inner_map.put(board,node);
        } else if(!inner_map.containsKey(board)){
            inner_map.put(board,node);
        }
    }

    public static GameTreeNode get(LocalState board){
        var inner_map = game_tree.get(board.GetMoveNumber());
        if(inner_map != null){
            return inner_map.get(board);
        }
        return null;
    }
}
