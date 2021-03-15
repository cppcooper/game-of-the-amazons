package structures;

import java.util.concurrent.ConcurrentHashMap;

public class GameTree {
    private static final ConcurrentHashMap<Integer,
            ConcurrentHashMap<LocalState,GameTreeNode>> game_tree = new ConcurrentHashMap<>();

    /* todo (1): provide way to find Best Move, (read more below)
    * There are basically two primary ways to go about this
    *  1) provide pre-processing which allows fast lookup of the Best Move
    *  2) search the game tree for the Best Move
    * To go about this, we're likely going to be looking deep down the game tree to the terminal states.
    * There is some question about whether we're going to propagate information up, and perhaps aggregate child node state evaluations into an average for the parent
    * ..or perhaps something else.. something more clever
    *
    * Depending on what we do, we may need to refactor the game tree to add backwards linking
    * this would obviously require unlinking nodes when pruning the game tree, which would increase the time complexity of pruning
    * */

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
