package structures;

import algorithms.analysis.MoveCompiler;

import java.util.ArrayList;
import java.util.HashMap;

public class MovePool {
    private static HashMap<Integer, Move> pool = new HashMap<>(1200000, 1.f);

    public static void generate_pool(){
        int[] all_positions = Debug.GetAllPositions();
        MoveCompiler.GetMoveList(new LocalState(),all_positions,true);
    }

    private static int make_key(int start, int next, int arrow){
        final int bits = 8;
        return (start << (bits << 1)) | (next << bits) | arrow;
    }
    
    public static Move get(int start, int next, int arrow) {
        int key = make_key(start, next, arrow);
        Move move = pool.get(key);
        if(move == null){
            move = new Move(start,next,arrow);
            pool.put(key,move);
        }
        return move;
    }

    public static void put(int start, int next, int arrow, Move move) {
        pool.put(make_key(start, next, arrow), move);
    }
}
